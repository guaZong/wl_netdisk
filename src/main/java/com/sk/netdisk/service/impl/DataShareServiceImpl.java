package com.sk.netdisk.service.impl;

import java.util.*;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.sk.netdisk.constant.RedisConstants;
import com.sk.netdisk.enums.AppExceptionCodeMsg;
import com.sk.netdisk.enums.DataEnum;
import com.sk.netdisk.exception.AppException;
import com.sk.netdisk.mapper.DataMapper;
import com.sk.netdisk.mapper.DataShareMapper;
import com.sk.netdisk.mapper.ShareMapper;
import com.sk.netdisk.pojo.Data;
import com.sk.netdisk.pojo.DataShare;
import com.sk.netdisk.pojo.Share;
import com.sk.netdisk.pojo.dto.ShareInfoDto;
import com.sk.netdisk.service.DataService;
import com.sk.netdisk.service.DataShareService;
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

/**
 * @author Administrator
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

    @Value("${domainName}")
    private String domainName;


    @Autowired
    public DataShareServiceImpl(DataMapper dataMapper, RedisUtil redisUtil, RedisTemplate redisTemplate,
                                DataShareMapper dataShareMapper, ShareMapper shareMapper, DataService dataService) {
        this.redisTemplate = redisTemplate;
        this.dataShareMapper = dataShareMapper;
        this.shareMapper = shareMapper;
        this.dataService = dataService;
        ThreadFactory namedThreadFactory = new NamedThreadFactory("DateShareServiceImpl", false);
        nowServiceThreadPool = new ThreadPoolExecutor(24, 24, 0,
                TimeUnit.SECONDS, new LinkedBlockingDeque<>(), namedThreadFactory);
        this.dataMapper = dataMapper;
        this.redisUtil = redisUtil;
    }

    @Override
    public DataShare createShareFile(List<Integer> dataIds, String passCode, Integer accessNum, Integer accessStatus, Integer expireDays) {
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
        String link = RandomUtil.randomString(randomKey, 25);
        DataShare dataShare;
        if (accessStatus == DataEnum.SHARE_IS_LIMIT.getIndex()) {
            dataShare = new DataShare(link, passCode, accessNum, DataEnum.SHARE_IS_LIMIT.getIndex(), new Date(), userId, expireDays);
        } else if (accessStatus == DataEnum.SHARE_NO_LIMIT.getIndex()) {
            dataShare = new DataShare(link, passCode, DataEnum.SHARE_NO_LIMIT.getIndex(), new Date(), userId, expireDays);
        } else {
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        this.save(dataShare);
        for (Share share : shareList) {
            share.setShareId(dataShare.getId());
        }
        shareMapper.batchSaveShare(shareList);
        nowServiceThreadPool.execute(() -> {
            createShareRedis(dataShare.getId());
            if (expireDays != DataEnum.SHARE_IS_BOUNDLESS.getIndex()) {
                //todo 如果expireDays不是-1就把他放入延迟队列中去
            }
        });
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
            //todo 如果shareId不是-1就删除延迟队列里的数据
        }
        shareMapper.deleteByShareId(shareId);
        this.removeById(shareId);
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
    public List<List<Data>> saveToMyResource(List<Integer> dataIds, Integer shareId, Integer targetFolderId, String code) {
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
        if(result.get(0).isEmpty()){
            //保存人数+1
            redisUtil.hincr(redisKey, "saveNum", 1);
        }
        //保存操作
        return result;
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




