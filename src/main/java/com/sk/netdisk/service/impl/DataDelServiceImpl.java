package com.sk.netdisk.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

import cn.hutool.core.thread.NamedThreadFactory;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sk.netdisk.constant.RedisConstants;
import com.sk.netdisk.enums.AppExceptionCodeMsg;
import com.sk.netdisk.enums.DataEnum;
import com.sk.netdisk.exception.AppException;
import com.sk.netdisk.mapper.*;
import com.sk.netdisk.pojo.Data;
import com.sk.netdisk.pojo.DataDel;
import com.sk.netdisk.pojo.File;
import com.sk.netdisk.pojo.QuickData;
import com.sk.netdisk.pojo.vo.DataDelInfoVo;
import com.sk.netdisk.service.DataDelService;
import com.sk.netdisk.util.CommonUtils;
import com.sk.netdisk.util.Redis.RedisUtil;
import com.sk.netdisk.util.UserUtil;
import com.sk.netdisk.util.upload.OSSUtil;
import com.sun.istack.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author lsj
* @description 针对表【data_del】的数据库操作Service实现
* @createDate 2023-05-03 15:28:35
*/
@Service
public class DataDelServiceImpl extends ServiceImpl<DataDelMapper, DataDel>
    implements DataDelService{

   private final DataDelMapper dataDelMapper;

   private final OSSUtil ossUtil;


   private final RedisUtil redisUtil;

   private final UserMapper userMapper;


   private final ShareMapper shareMapper;

   private final DataMapper dataMapper;

   private final FileMapper fileMapper;

    private final ExecutorService nowServiceThreadPool;

    private final ExecutorService recurHelpThreadPool;

    private final QuickDataMapper quickDataMapper;



    @Autowired
    public DataDelServiceImpl(DataDelMapper dataDelMapper, OSSUtil ossUtil,
                              RedisUtil redisUtil, UserMapper userMapper,
                              ShareMapper shareMapper, DataMapper dataMapper, FileMapper fileMapper, QuickDataMapper quickDataMapper) {
        this.quickDataMapper = quickDataMapper;
        ThreadFactory namedThreadFactory1 = new NamedThreadFactory("DataDelServiceImpl", false);
        nowServiceThreadPool = new ThreadPoolExecutor(24, 24, 0,
                TimeUnit.SECONDS, new LinkedBlockingDeque<>(), namedThreadFactory1);

        ThreadFactory namedThreadFactory2 = new NamedThreadFactory("recurHelp", false);
        recurHelpThreadPool = new ThreadPoolExecutor(24, 24, 0,
                TimeUnit.SECONDS, new LinkedBlockingDeque<>(), namedThreadFactory2);
        this.dataDelMapper = dataDelMapper;
        this.ossUtil = ossUtil;
        this.redisUtil = redisUtil;
        this.userMapper = userMapper;
        this.shareMapper = shareMapper;
        this.dataMapper = dataMapper;
        this.fileMapper = fileMapper;
    }

    @Override
    public DataDel insertDataDel(Integer dataId,Integer userId) {
        DataDel dataDel=new DataDel();
        dataDel.setDataId(dataId);
        dataDel.setCreateBy(userId);
        dataDel.setCreateTime(new Date());
        this.save(dataDel);
        return dataDel;
    }

    @Override
    public boolean deleteDataDel(Integer dataDelId) {
        return this.removeById(dataDelId);
    }

    @Override
    public boolean batchDeleteDataDel(List<Integer> dataDelIds) {
        return this.removeBatchByIds(dataDelIds);
    }

    @Override
    public List<DataDelInfoVo> infoAllDataDel(Integer userId) {
        return dataDelMapper.infoAllDataDel(userId);
    }

    @Override
    @Transactional
    public void finalDelData(Integer dataDelId, @Nullable String code, Integer result) {
        Integer userId = UserUtil.getLoginUserId();
        DataDel dataDel = this.getById(dataDelId);
        if (Objects.isNull(dataDel)) {
            throw new AppException(AppExceptionCodeMsg.DATA_NOT_EXISTS);
        }
        int accessToFinalDelData = DataEnum.ACCESS_TO_FINAL_DEL.getIndex();
        if ((result != accessToFinalDelData && Objects.isNull(code)) || !dataDel.getCreateBy().equals(userId)) {
            throw new AppException(AppExceptionCodeMsg.INVALID_PERMISSION);
        }
        if (result != accessToFinalDelData && !Objects.equals(code,
                redisUtil.get(RedisConstants.PHONE_FINAL_DEL_KEY + userMapper.selectById(userId).getUsername()))) {
            throw new AppException(AppExceptionCodeMsg.INVALID_CODE);
        }
        finalDeleteData(dataDelId, dataDel.getDataId(), userId);
    }

    /**
     * 删除回收站文件方法
     *
     * @param dataDelId 回收站文件id
     * @param dataId    文件id
     * @param userId    用户id
     */
    private void finalDeleteData(Integer dataDelId, Integer dataId, Integer userId) {
        redisUtil.set(RedisConstants.FINAL_DEL_KEY + userId, 1, 60 * 60 * 6);
        Data data = dataMapper.findById(dataId);
        dataMapper.finalDelData(dataId);
        recurCountFinalDelete(data);
        this.removeById(dataDelId);
        finalDelOther(dataId);
    }

    private void finalDelOther(Integer dataId) {
        quickDataMapper.delete(new QueryWrapper<QuickData>().eq("data_id", dataId));
    }

    /**
     * 递归删除回收站文件
     *
     * @param data 文件
     */
    public void recurCountFinalDelete(Data data) {
        if (data.getType() != DataEnum.FOLDER.getIndex()) {
            File file = fileMapper.selectById(data.getFileId());
            if (!Objects.isNull(file)) {
                redisUtil.hdecr(RedisConstants.FILE_KEY + file.getMd5(), "useNum", 1);
                Object useNum = redisUtil.hget(RedisConstants.FILE_KEY + file.getMd5(), "useNum");
                if ((int) useNum <= 0) {
                    ossUtil.removeData(CommonUtils.getObjectName(file.getLink()));
                    redisUtil.del(RedisConstants.FILE_KEY + file.getMd5());
                    fileMapper.deleteById(data.getFileId());
                }
            }
        }
        List<Data> dataList = dataMapper.findDelData(data.getId());
        for (Data subData : dataList) {
            recurHelpThreadPool.execute(() -> recurCountFinalDelete(subData));
        }
        dataMapper.finalDelData(data.getId());
        finalDelOther(data.getId());
    }

    @Override
    @Transactional
    public void batchFinalDelData(List<Integer> dataDelIds, @Nullable String code, Integer result) {
        Integer userId = UserUtil.getLoginUserId();
        int accessToFinalDelData = DataEnum.ACCESS_TO_FINAL_DEL.getIndex();
        if (result != accessToFinalDelData && !Objects.equals(code,
                redisUtil.get(RedisConstants.PHONE_FINAL_DEL_KEY + userMapper.selectById(userId).getUsername()))) {
            throw new AppException(AppExceptionCodeMsg.INVALID_CODE);
        }
        CountDownLatch countDownLatch = new CountDownLatch(dataDelIds.size());
        for (int dataDelId : dataDelIds) {
            nowServiceThreadPool.execute(() -> {
                try {
                    DataDel dataDel = this.getById(dataDelId);
                    if ((result != accessToFinalDelData && Objects.isNull(code))
                            || (!Objects.isNull(dataDel) && !dataDel.getCreateBy().equals(userId))
                            || Objects.isNull(dataDel)) {
                        return;
                    }
                    redisUtil.set(RedisConstants.FINAL_DEL_KEY + userId, 1, 60 * 60 * 6);
                    Data data = dataMapper.findById(dataDel.getDataId());
                    dataMapper.finalDelData(dataDel.getDataId());
                    recurCountFinalDelete(data);
                    this.removeById(dataDelId);
                    finalDelOther(data.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        try {
            countDownLatch.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否有权限删除回收站文件
     *
     * @return Integer 1代表有权限,-1代表无权限
     */
    @Override
    public Integer judgeSendDelCode() {
        Integer userId = UserUtil.getLoginUserId();
        Object code = redisUtil.get(RedisConstants.FINAL_DEL_KEY + userId);
        return code != null ? 1 : -1;
    }

}




