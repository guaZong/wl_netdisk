package com.netdisk.system.service.impl;

import java.util.*;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.netdisk.common.enums.AppExceptionCodeMsg;
import com.netdisk.common.enums.DataEnum;
import com.netdisk.common.exception.AppException;
import com.netdisk.common.utils.CommonUtils;
import com.netdisk.common.utils.UserUtil;
import com.netdisk.system.dto.DataDetInfoDto;
import com.netdisk.system.dto.ShareInfoDto;
import com.netdisk.system.mapper.DataMapper;
import com.netdisk.system.mapper.DataShareMapper;
import com.netdisk.system.mapper.ShareMapper;
import com.netdisk.system.pojo.Data;
import com.netdisk.system.pojo.DataShare;
import com.netdisk.system.pojo.Share;
import com.netdisk.system.pojo.ShareDetail;
import com.netdisk.system.service.DataService;
import com.netdisk.system.service.DataShareService;
import com.netdisk.system.service.ShareDetailService;
import com.netdisk.common.utils.Redis.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.*;

/**
 * @author lsj
 * @description 针对表【data_share】的数据库操作Service实现
 * @createDate 2022-11-29 10:50:12
 */
@Service
public class DataShareServiceImpl extends ServiceImpl<DataShareMapper, DataShare>
        implements DataShareService {

    final private DataMapper dataMapper;

    final private RedisUtil redisUtil;

    final private ThreadPoolExecutor nowServiceThreadPool;

    final private DataShareMapper dataShareMapper;

    final private ShareMapper shareMapper;

    final private DataService dataService;

    final private DataServiceImpl dataServiceImpl;

    private final ShareDetailService shareDetailService;

    @Value("${domainName}")
    private String domainName;


    @Autowired
    public DataShareServiceImpl(DataMapper dataMapper, RedisUtil redisUtil, DataShareMapper dataShareMapper,
                                ShareMapper shareMapper, DataService dataService, DataServiceImpl dataServiceImpl,
                                ShareDetailService shareDetailService) {
        this.dataShareMapper = dataShareMapper;
        this.shareMapper = shareMapper;
        this.dataService = dataService;
        this.dataServiceImpl = dataServiceImpl;
        this.shareDetailService = shareDetailService;
        ThreadFactory namedThreadFactory = new NamedThreadFactory("DateShareServiceImpl", false);
        nowServiceThreadPool = new ThreadPoolExecutor(24, 24, 0,
                TimeUnit.SECONDS, new LinkedBlockingDeque<>(), namedThreadFactory);
        this.dataMapper = dataMapper;
        this.redisUtil = redisUtil;
    }

    @Override
    @Transactional
    public DataShare createShareFile(List<Integer> dataIds, String passCode, Integer accessNum,
                                     Integer accessStatus, Integer expireDays) {

        Integer userId = UserUtil.getLoginUserId();
        List<Data> dataList = dataMapper.selectBatchIds(dataIds);
        if (dataList.isEmpty()) {
            throw new AppException(AppExceptionCodeMsg.DATA_NOT_EXISTS);
        }
        List<Share> shareList = new ArrayList<>();
        for (Data data : dataList) {
            if (Objects.isNull(data)) {
                throw new AppException(AppExceptionCodeMsg.DATA_NOT_EXISTS);
            }
            if (!userId.equals(data.getCreateBy())) {
                throw new AppException(AppExceptionCodeMsg.INVALID_PERMISSION);
            }
            Share share = new Share();
            share.setDataId(data.getId());
            shareList.add(share);
        }
        passCode = StringUtils.isEmpty(passCode) ? "" : passCode;
        //创建分享uuid
        String randomKey = "_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789-";
        String uuid = RandomUtil.randomString(randomKey, 25);
        //创建分享链接
        String link = domainName + "sysShare/" + uuid;

        DataShare dataShare;
        //如果分享限制人数
        if (accessStatus == DataEnum.SHARE_IS_LIMIT.getIndex()) {
            if (Objects.isNull(accessNum) || accessNum == 0) {
                throw new AppException(AppExceptionCodeMsg.NULL_VALUE);
            }
            dataShare = new DataShare(uuid, passCode, accessNum, DataEnum.SHARE_IS_LIMIT.getIndex(), new Date(), userId, expireDays);
        } else if (accessStatus == DataEnum.SHARE_NO_LIMIT.getIndex()) {
            //如果不限制人数
            dataShare = new DataShare(uuid, passCode, DataEnum.SHARE_NO_LIMIT.getIndex(), new Date(), userId, expireDays);
        } else {
            //如果状态不正确
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        //db生成分享文件数据
        this.save(dataShare);
        for (Share share : shareList) {
            share.setShareId(dataShare.getId());
        }
        if (shareList.isEmpty()) {
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        shareMapper.batchSaveShare(shareList);
        shareDetailService.createShareDetail(dataShare.getId());
        if (expireDays != DataEnum.SHARE_IS_BOUNDLESS.getIndex()) {
            //todo 如果expireDays不是-1就把他放入延迟队列中去
        }
        dataShare.setDataIds(new HashSet<>(dataIds));
        dataShare.setLink(link);
        return dataShare;
    }

    @Override
    public void cancelShare(Integer shareId, Integer userId) {
        DataShare dataShare = dataShareMapper.selectById(shareId);
        if (Objects.isNull(dataShare)) {
            throw new AppException(AppExceptionCodeMsg.DATA_NOT_EXISTS);
        }
        if (!userId.equals(dataShare.getCreateBy())) {
            throw new AppException(AppExceptionCodeMsg.INVALID_PERMISSION);
        }
        if (dataShare.getExpireDays() != DataEnum.SHARE_IS_BOUNDLESS.getIndex()) {
            //todo 如果expireDays不是-1就删除延迟队列里的数据
        }
        shareMapper.deleteByShareId(shareId);
        shareDetailService.deleteShareDetail(dataShare.getId());
        dataShareMapper.deleteById(shareId);
    }

    @Override
    public void batchCancelShare(List<Integer> shareIds) {
        Integer userId = UserUtil.getLoginUserId();
        CountDownLatch countDownLatch = new CountDownLatch(shareIds.size());
        for (int shareId : shareIds) {
            nowServiceThreadPool.execute(() -> {
                try {
                    DataShare dataShare = dataShareMapper.selectById(shareId);
                    if (Objects.isNull(dataShare)) {
                        return;
                    }
                    cancelShare(shareId, userId);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        try {
            boolean await = countDownLatch.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ShareInfoDto> traverseShares() {
        Integer userId = UserUtil.getLoginUserId();
        List<ShareInfoDto> shareInfoDtoList = dataShareMapper.traverseShares(userId);
        CountDownLatch countDownLatch = new CountDownLatch(shareInfoDtoList.size());
        for (ShareInfoDto shareInfo : shareInfoDtoList) {
            nowServiceThreadPool.execute(() -> {
                try {
                    List<Integer> dataIds = shareMapper.selectIdsByShareId(shareInfo.getDataShareId());
                    shareInfo.setLink(getLink(shareInfo.getLink()));
                    shareInfo.setDataIds(dataIds);
                    shareInfo.setNameList(dataMapper.findNameByIds(dataIds));
                    shareInfo.setType(dataMapper.findTypeByIds(dataIds));
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        try {
            boolean await = countDownLatch.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return shareInfoDtoList;
    }

    @Override
    @Transactional
    public void saveToMyResource(List<Integer> dataIds, Integer shareId, Integer targetFolderId, String code) {
        Integer userId = UserUtil.getLoginUserId();
        DataShare dataShare = dataShareMapper.selectById(shareId);
        if (Objects.isNull(dataShare)) {
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        if (!StringUtils.isEmpty(dataShare.getPassCode()) && !code.equals(dataShare.getPassCode())) {
            throw new AppException(AppExceptionCodeMsg.PASSCODE_INVALID);
        }
        if (userId.equals(dataShare.getCreateBy())) {
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        //todo 此处并发安全有问题,多个并发操作同时保存会出问题,这里需要上锁
        if (dataShare.getAccessStatus() == DataEnum.SHARE_IS_LIMIT.getIndex()) {
            ShareDetail shareDetail = shareDetailService.getShareDetailInfo(shareId);
            Integer shareNum = shareDetail.getSaveNum();
            if (!Objects.isNull(shareNum) &&  shareNum > dataShare.getAccessNum()) {
                //todo 分享(保存)人数到达上限进行删除或者修改操作
                throw new AppException(AppExceptionCodeMsg.SHARE_INVALID);
            }
        }
        List<List<Data>> result = dataService.copyToNewFolder(dataIds, targetFolderId);
        //如果没有重名直接保存
        if (result.get(0).isEmpty()) {
           shareDetailService.addSaveNum(shareId);
           return;
        }
        //如果出现重名情况，首先获取result重名的文件
        List<Data> reNameDataList = result.get(0);
        List<String> nameList = dataMapper.findNameByParentDataId(targetFolderId, userId);
        CountDownLatch countDownLatch = new CountDownLatch(reNameDataList.size());
        for (Data renameData : reNameDataList) {
            nowServiceThreadPool.execute(() -> {
                try {
                    renameData.setName(CommonUtils.renameFile(renameData.getName(), nameList));
                    Data newData = new Data(renameData.getName(), renameData.getType(), targetFolderId,
                            renameData.getCreateTime(), renameData.getUpdateTime(), userId, renameData.getFileId());
                    dataMapper.insert(newData);
                    dataServiceImpl.recurToCopy(renameData, userId, newData.getId());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        try {
            countDownLatch.await();
            shareDetailService.addSaveNum(shareId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<DataDetInfoDto> getShareData(String uuid, String passCode) {
        //获取该分享的一些信息
        DataShare dataShare = this.getOne(new QueryWrapper<DataShare>().eq("link", uuid));
        if (Objects.isNull(dataShare) || dataShare.getAccessStatus() == DataEnum.SHARE_IS_EXPIRE.getIndex()) {
            throw new AppException(AppExceptionCodeMsg.SHARE_INVALID);
        }
        if (dataShare.getAccessStatus() == DataEnum.SHARE_IS_DELETE.getIndex()) {
            throw new AppException(AppExceptionCodeMsg.SHARE_IS_DELETE);
        }
        if (!StringUtils.isEmpty(dataShare.getPassCode()) && !dataShare.getPassCode().equals(passCode)) {
            throw new AppException(AppExceptionCodeMsg.PASSCODE_INVALID);
        }

        List<Integer> dataIds = shareMapper.selectIdsByShareId(dataShare.getId());
        if (dataIds.isEmpty()) {
            throw new AppException(AppExceptionCodeMsg.SHARE_INVALID);
        }
        List<DataDetInfoDto> dataList = dataMapper.findDataByIds(dataIds);
        if (dataList.isEmpty()) {
            this.update(new DataShare(), new UpdateWrapper<DataShare>()
                    .set("access_status", DataEnum.SHARE_IS_DELETE.getIndex())
                    .eq("id", dataShare.getId())
                    .ne("access_status",DataEnum.SHARE_IS_DELETE.getIndex()));
            throw new AppException(AppExceptionCodeMsg.SHARE_IS_DELETE);
        }
        shareDetailService.addLookNum(dataShare.getId());
        return dataList;
    }

    @Override
    public List<DataDetInfoDto> infoShareData(Integer parentDataId, String passCode, Integer shareId) {
        DataShare dataShare = this.getById(shareId);

        if (Objects.isNull(dataShare)) {
            throw new AppException(AppExceptionCodeMsg.SHARE_INVALID);
        }

        String sharePassCode = dataShare.getPassCode();
        if (!StringUtils.isEmpty(sharePassCode) && !sharePassCode.equals(passCode)) {
            throw new AppException(AppExceptionCodeMsg.PASSCODE_INVALID);
        }
        List<Integer> shareList =shareMapper.selectIdsByShareId(shareId);
        if (shareList.isEmpty()) {
            throw new AppException(AppExceptionCodeMsg.SHARE_INVALID);
        }
        boolean permission = dataService.judgeDataFather(parentDataId, shareList);
        //没有权限访问
        if(!permission){
            throw new AppException(AppExceptionCodeMsg.INVALID_PERMISSION);
        }
        return dataMapper.visitorInfoData(parentDataId);
    }

    @Override
    public Integer findIdByUidAndCode(String uuid, String passCode) {
        DataShare dataShare = this.getOne(new QueryWrapper<DataShare>().eq("link", uuid));
        if (Objects.isNull(dataShare)) {
            throw new AppException(AppExceptionCodeMsg.SHARE_INVALID);
        }
        return dataShare.getId();
    }

    @Override
    public void judgeUpdateDataShare(Integer dataId) {
        Share share = shareMapper.selectOne(new QueryWrapper<Share>().eq("data_id", dataId));
        if (Objects.isNull(share)) {
            return;
        }
        Integer dataShareId = share.getShareId();
        shareMapper.deleteByDataId(dataId);
        List<Share> shareList = shareMapper.selectList(new QueryWrapper<Share>().eq("share_id", dataShareId));
        if (shareList.isEmpty()) {
            this.update(new DataShare(), new UpdateWrapper<DataShare>()
                    .set("access_status", DataEnum.SHARE_IS_DELETE.getIndex())
                    .eq("id", dataShareId));
        }
    }

    /**
     * 获取完整link
     *
     * @param link link
     * @return String
     */
    private String getLink(String link) {
        return domainName + "sysShare/" + link;
    }

}




