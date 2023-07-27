package com.sk.netdisk.service.impl;

import java.util.*;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.sk.netdisk.constant.RedisConstants;
import com.sk.netdisk.enums.AppExceptionCodeMsg;
import com.sk.netdisk.enums.DataEnum;
import com.sk.netdisk.exception.AppException;
import com.sk.netdisk.mapper.DataMapper;
import com.sk.netdisk.mapper.DataShareMapper;
import com.sk.netdisk.mapper.ShareMapper;
import com.sk.netdisk.pojo.*;
import com.sk.netdisk.pojo.dto.DataDetInfoDto;
import com.sk.netdisk.pojo.dto.ShareInfoDto;
import com.sk.netdisk.service.DataService;
import com.sk.netdisk.service.DataShareService;
import com.sk.netdisk.util.CommonUtils;
import com.sk.netdisk.util.Redis.RedisUtil;
import com.sk.netdisk.util.UserUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

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

    final private RedisTemplate<String, Integer> redisTemplate;

    final private DataShareMapper dataShareMapper;

    final private ShareMapper shareMapper;

    final private DataService dataService;

    final private DataServiceImpl dataServiceImpl;

    @Value("${domainName}")
    private String domainName;


    @Autowired
    public DataShareServiceImpl(DataMapper dataMapper, RedisUtil redisUtil, RedisTemplate redisTemplate,
                                DataShareMapper dataShareMapper, ShareMapper shareMapper, DataService dataService, DataServiceImpl dataServiceImpl) {
        this.redisTemplate = redisTemplate;
        this.dataShareMapper = dataShareMapper;
        this.shareMapper = shareMapper;
        this.dataService = dataService;
        this.dataServiceImpl = dataServiceImpl;
        ThreadFactory namedThreadFactory = new NamedThreadFactory("DateShareServiceImpl", false);
        nowServiceThreadPool = new ThreadPoolExecutor(24, 24, 0,
                TimeUnit.SECONDS, new LinkedBlockingDeque<>(), namedThreadFactory);
        this.dataMapper = dataMapper;
        this.redisUtil = redisUtil;
    }

    @Override
    @Transactional
    public DataShare createShareFile(List<Integer> dataIds, String passCode,
                                     Integer accessNum, Integer accessStatus, Integer expireDays) {
        Integer userId = UserUtil.getLoginUserId();
        List<Data> dataList = dataMapper.selectBatchIds(dataIds);
        List<Share> shareList = new ArrayList<>();
        for (Data data : dataList) {
            if (dataList.isEmpty()) {
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
        String randomKey = "_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789-";
        String uuid = RandomUtil.randomString(randomKey, 25);
        String link = domainName + "sysShare/" + uuid;
        DataShare dataShare;
        if (accessStatus == DataEnum.SHARE_IS_LIMIT.getIndex()) {
            if (Objects.isNull(accessNum) || accessNum == 0) {
                throw new AppException(AppExceptionCodeMsg.NULL_VALUE);
            }
            dataShare = new DataShare(uuid, passCode, accessNum, DataEnum.SHARE_IS_LIMIT.getIndex(),
                    new Date(), userId, expireDays);
        } else if (accessStatus == DataEnum.SHARE_NO_LIMIT.getIndex()) {
            dataShare = new DataShare(uuid, passCode, DataEnum.SHARE_NO_LIMIT.getIndex(),
                    new Date(), userId, expireDays);
        } else {
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        this.save(dataShare);
        for (Share share : shareList) {
            share.setShareId(dataShare.getId());
        }
        if (shareList.isEmpty()) {
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        shareMapper.batchSaveShare(shareList);
        nowServiceThreadPool.execute(() -> {
            createShareRedis(dataShare.getId());
            if (expireDays != DataEnum.SHARE_IS_BOUNDLESS.getIndex()) {
                //todo 如果expireDays不是-1就把他放入延迟队列中去
            }
        });
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
        redisUtil.del(RedisConstants.SHARE_KEY + shareId);
        if (dataShare.getExpireDays() != DataEnum.SHARE_IS_BOUNDLESS.getIndex()) {
            //todo 如果expireDays不是-1就删除延迟队列里的数据
        }
        shareMapper.deleteByShareId(shareId);
        redisUtil.del(RedisConstants.SHARE_KEY + shareId);
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
        HashOperations<String, String, Integer> hashOperations = redisTemplate.opsForHash();
        CountDownLatch countDownLatch = new CountDownLatch(shareInfoDtoList.size());
        for (ShareInfoDto shareInfo : shareInfoDtoList) {
            nowServiceThreadPool.execute(() -> {
                try {
                    List<Integer> dataIds = shareMapper.selectIdsByShareId(shareInfo.getId());
                    Map<String, Integer> redisMap = hashOperations.entries(RedisConstants.SHARE_KEY + shareInfo.getId());
                    Integer lookNum = redisMap.get("lookNum");
                    Integer saveNum = redisMap.get("saveNum");
                    Integer downloadNum = redisMap.get("downloadNum");
                    Integer shareNum = redisMap.get("shareNum");
                    shareInfo.setShareNum(shareNum);
                    shareInfo.setLookNum(lookNum);
                    shareInfo.setSaveNum(saveNum);
                    shareInfo.setDownloadNum(downloadNum);
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
        String redisKey = RedisConstants.SHARE_KEY + shareId;
        if (dataShare.getAccessStatus() == DataEnum.SHARE_IS_LIMIT.getIndex()) {
            Object shareNum = redisUtil.hget(redisKey, "shareNum");
            if (!Objects.isNull(shareNum) && (int) shareNum > dataShare.getAccessNum()) {
                //todo 分享人数到达上限进行删除或者修改操作
                throw new AppException(AppExceptionCodeMsg.SHARE_INVALID);
            }
        }
        List<List<Data>> result = dataService.copyToNewFolder(dataIds, targetFolderId);
        if (result.get(0).isEmpty()) {
            redisUtil.hincr(redisKey, "saveNum", 1);
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
            redisUtil.hincr(redisKey, "saveNum", 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<DataDetInfoDto> getShareData(String uuid, String passCode) {
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
                    .set("access_status", DataEnum.SHARE_IS_DELETE.getIndex()).eq("id", dataShare.getId()));
            throw new AppException(AppExceptionCodeMsg.SHARE_IS_DELETE);
        }
        redisUtil.hincr(RedisConstants.SHARE_KEY + dataShare.getId(), "lookNum", 1);
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
        System.out.println(share);
        if (Objects.isNull(share)) {
            return;
        }
        Integer dataShareId = share.getShareId();
        shareMapper.deleteByDataId(dataId);
        List<Share> shareList = shareMapper.selectList(new QueryWrapper<Share>().eq("share_id", dataShareId));
        System.out.println(shareList);
        if (shareList.isEmpty()) {
            this.update(new DataShare(), new UpdateWrapper<DataShare>()
                    .set("access_status", DataEnum.SHARE_IS_DELETE.getIndex())
                    .eq("id", dataShareId));
        }
    }


    /**
     * 创建分享文件相关redis
     *
     * @param shareId shareId
     */
    private void createShareRedis(Integer shareId) {
        //redis key
        String key = RedisConstants.SHARE_KEY + shareId;
        //查看次数
        redisUtil.hset(key, "lookNum", 0);
        //分享次数
        redisUtil.hset(key, "shareNum", 0);
        //保存次数
        redisUtil.hset(key, "saveNum", 0);
        //下载次数
        redisUtil.hset(key, "downloadNum", 0);
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




