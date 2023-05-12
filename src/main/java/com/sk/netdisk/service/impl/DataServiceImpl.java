package com.sk.netdisk.service.impl;


import java.util.*;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.sk.netdisk.constant.RabbitmqConstants;
import com.sk.netdisk.enums.AppExceptionCodeMsg;
import com.sk.netdisk.enums.DataEnum;
import com.sk.netdisk.exception.AppException;
import com.sk.netdisk.mapper.DataMapper;
import com.sk.netdisk.mapper.FileMapper;
import com.sk.netdisk.mapper.UserMapper;
import com.sk.netdisk.pojo.Data;
import com.sk.netdisk.pojo.DataDel;
import com.sk.netdisk.pojo.File;
import com.sk.netdisk.pojo.dto.DataDetInfoDto;
import com.sk.netdisk.pojo.dto.DataPathDto;
import com.sk.netdisk.pojo.vo.DataDelInfoVo;
import com.sk.netdisk.pojo.vo.DataInfoVo;
import com.sk.netdisk.service.DataDelService;
import com.sk.netdisk.service.DataService;
import com.sk.netdisk.util.CommonUtils;
import com.sk.netdisk.constant.RedisConstants;
import com.sk.netdisk.util.Redis.RedisUtil;
import com.sk.netdisk.util.UserUtil;
import com.sk.netdisk.util.upload.OSSUtil;
import com.sk.netdisk.util.upload.UploadUtil;
import com.sun.istack.Nullable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【data】的数据库操作Service实现
 * @createDate 2022-11-29 10:50:12
 */
@Service
@Slf4j
public class DataServiceImpl extends ServiceImpl<DataMapper, Data>
        implements DataService {

    private final DataMapper dataMapper;

    private final RedisUtil redisUtil;

    private final DataDelService dataDelService;

    private final OSSUtil ossUtil;

    private final UserMapper userMapper;

    private final FileMapper fileMapper;

    private final ExecutorService nowServiceThreadPool;

    private final RabbitTemplate rabbitTemplate;

    private final ExecutorService recurHelpThreadPool;

    @Autowired
    public DataServiceImpl(DataMapper dataMapper, RedisUtil redisUtil,
                           DataDelService dataDelService, OSSUtil ossUtil,
                           UserMapper userMapper, FileMapper fileMapper, RabbitTemplate rabbitTemplate) {


        ThreadFactory namedThreadFactory1 = new NamedThreadFactory("DataServiceImpl", false);
        nowServiceThreadPool = new ThreadPoolExecutor(24, 24, 0,
                TimeUnit.SECONDS, new LinkedBlockingDeque<>(), namedThreadFactory1);

        ThreadFactory namedThreadFactory2 = new NamedThreadFactory("recurHelp", false);
        recurHelpThreadPool = new ThreadPoolExecutor(24, 24, 0,
                TimeUnit.SECONDS, new LinkedBlockingDeque<>(), namedThreadFactory2);
        this.dataMapper = dataMapper;
        this.redisUtil = redisUtil;
        this.dataDelService = dataDelService;
        this.ossUtil = ossUtil;
        this.userMapper = userMapper;
        this.fileMapper = fileMapper;
        this.rabbitTemplate = rabbitTemplate;
    }


    @Override
    public List<DataDetInfoDto> infoData(Integer parentDataId) {
        Integer userId = UserUtil.getLoginUserId();
        Data data = this.getById(parentDataId);
        if (parentDataId != DataEnum.MAX_NONE_FOLDER.getIndex() && Objects.isNull(data)) {
            throw new AppException(AppExceptionCodeMsg.FOLDER_NOT_EXISTS);
        }
        if (!Objects.isNull(data) && data.getType() != DataEnum.FOLDER.getIndex()) {
            throw new AppException(AppExceptionCodeMsg.DATA_NOT_ENTER);
        }
        return dataMapper.findListByCreateByAndParentIdInnerFileId(parentDataId, userId);
    }

    @Override
    public DataInfoVo getDataInfo(Integer dataId) {
        if (dataId == DataEnum.MAX_NONE_FOLDER.getIndex()) {
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        Integer userId = UserUtil.getLoginUserId();
        Data data = this.getById(dataId);
        if (Objects.isNull(data)) {
            throw new AppException(AppExceptionCodeMsg.DATA_NOT_EXISTS);
        }
        if (!data.getCreateBy().equals(userId)) {
            throw new AppException(AppExceptionCodeMsg.INVALID_PERMISSION);
        }
        int folderNum = 0, fileNum = 0;
        long dataSize = 0;
        RecurInfo recurInfo = new RecurInfo(folderNum, fileNum, dataSize);
        Date date;
        if (Objects.isNull(data.getUpdateTime())) {
            date = data.getCreateTime();
        } else {
            date = data.getUpdateTime();
        }
        String stringDate = CommonUtils.getStringDate(date);
        recurCountSize(dataId, recurInfo);
        StringBuilder nameBuilder = new StringBuilder();
        recurCountName(data.getParentDataId(), nameBuilder);
        DataInfoVo dataInfoVo;
        if (data.getType() == DataEnum.FOLDER.getIndex()) {
            String fileSize = getFileSize(recurInfo.dataSize);
            dataInfoVo = new DataInfoVo("文件夹", "/" + nameBuilder.toString(), fileSize,
                    recurInfo.folderNum, recurInfo.fileNum, stringDate);
        } else {
            String[] split = data.getName().split("\\.");
            String str = split[1] + "文件";
            Integer fileId = this.getById(dataId).getFileId();
            File file = fileMapper.selectById(fileId);
            dataInfoVo = new DataInfoVo(str, "/" + nameBuilder.toString(), file.getSize(),
                    null, null, stringDate);
        }
        return dataInfoVo;
    }

    /**
     * 递归计算 文件路径
     *
     * @param parentDataId 父文件夹id
     * @param nameBuilder  拼接文件名的对象
     */
    private void recurCountName(Integer parentDataId, StringBuilder nameBuilder) {
        if (parentDataId == null || parentDataId == 0) {
            return;
        }
        Data data = this.getById(parentDataId);
        if (data != null) {
            if (nameBuilder.length() > 0) {
                nameBuilder.insert(0, "/");
            }
            nameBuilder.insert(0, data.getName());
            recurCountName(data.getParentDataId(), nameBuilder);
        }
    }

    @Override
    public List<DataDelInfoVo> infoDataDel() {
        Integer userId = UserUtil.getLoginUserId();
        return dataDelService.infoAllDataDel(userId);
    }

    @Override
    public Integer getParentDataId(Integer nowDataId) {
        if (nowDataId == DataEnum.MAX_NONE_FOLDER.getIndex()) {
            throw new AppException(AppExceptionCodeMsg.FOLDER_NOT_EXISTS);
        }
        Data nowData = this.getById(nowDataId);
        if (Objects.isNull(nowData)) {
            throw new AppException(AppExceptionCodeMsg.DATA_NOT_EXISTS);
        }
        return nowData.getParentDataId();
    }

    @Override
    public List<Data> getDataFolder(Integer folderId) {
        Integer userId = UserUtil.getLoginUserId();
        return this.list(new QueryWrapper<Data>()
                .eq("parent_data_id", folderId)
                .eq("create_by", userId)
                .eq("type", DataEnum.FOLDER.getIndex()));
    }

    @Override
    public List<Data> traverseQuickAccess() {
        return null;
    }

    @Override
    public List<DataPathDto> getDataPath(Integer dataId) {
        List<DataPathDto> result = new ArrayList<>();
        if (dataId == DataEnum.MAX_NONE_FOLDER.getIndex()) {
            result.add(new DataPathDto(0, "/"));
            return result;
        }
        Integer userId = UserUtil.getLoginUserId();
        Data data = this.getById(dataId);
        if (Objects.isNull(data)) {
            throw new AppException(AppExceptionCodeMsg.DATA_NOT_EXISTS);
        }
        if (!data.getCreateBy().equals(userId)) {
            throw new AppException(AppExceptionCodeMsg.INVALID_PERMISSION);
        }
        recurToCountPath(data, result);
        Collections.reverse(result);
        result.add(new DataPathDto(dataId, data.getName()));
        return result;
    }

    private void recurToCountPath(Data data, List<DataPathDto> result) {
        if (data.getParentDataId() == DataEnum.MAX_NONE_FOLDER.getIndex()) {
            result.add(new DataPathDto(0, "/"));
            return;
        }
        Data parentData = this.getOne(new QueryWrapper<Data>().eq("id", data.getParentDataId()));
        result.add(new DataPathDto(parentData.getId(), parentData.getName()));
        recurToCountPath(parentData, result);
    }

    @Override
    public Data createFolder(Integer parentDataId, String folderName) {
        Integer userId = UserUtil.getLoginUserId();
        Data data = new Data();
        int folderType = DataEnum.FOLDER.getIndex();
        String judgeReName = judgeReName(folderName, parentDataId, folderType, userId);
        data.setName(judgeReName);
        data.setType(folderType);
        data.setCreateTime(new Date());
        data.setCreateBy(userId);
        if (parentDataId == DataEnum.MAX_NONE_FOLDER.getIndex()) {
            data.setParentDataId(DataEnum.MAX_NONE_FOLDER.getIndex());
        } else {
            Data fatherData = this.getById(parentDataId);
            if (Objects.isNull(fatherData)) {
                throw new AppException(AppExceptionCodeMsg.FOLDER_NOT_EXISTS);
            }
            data.setParentDataId(parentDataId);
        }
        this.save(data);
        return data;
    }


    @Override
    @Transactional
    public Integer uploadMinData(MultipartFile[] files, Integer targetFolderDataId) throws Exception {
        Integer userId = UserUtil.getLoginUserId();
        Data parentData;
        int uploadNum = 0;
        if (targetFolderDataId != DataEnum.MAX_NONE_FOLDER.getIndex()) {
            parentData = this.getById(targetFolderDataId);
            if (Objects.isNull(parentData) || parentData.getType() != DataEnum.FOLDER.getIndex()) {
                throw new AppException(AppExceptionCodeMsg.FOLDER_NOT_EXISTS);
            }
            if (!parentData.getCreateBy().equals(userId)) {
                throw new AppException(AppExceptionCodeMsg.INVALID_PERMISSION);
            }
        }
        for (MultipartFile file : files) {
            Data data = createData(file, targetFolderDataId);
            if (Objects.isNull(data)) {
                continue;
            }
            String fileMd5 = CommonUtils.getFileMd5(file);
            Object fileId = redisUtil.hget(RedisConstants.FILE_KEY + fileMd5, "fileId");
            if (Objects.isNull(fileId)) {
                String path = ossUtil.easyUpload(file);
                File newFile = createFile(fileMd5, path, userId, getFileSize(file.getSize()), String.valueOf(file.getSize()));
                fileMapper.insert(newFile);
                data.setFileId(newFile.getId());
                redisStorageFile(fileMd5, newFile.getId(), file.getSize());
            } else {
                File fileById = fileMapper.selectById((int) fileId);
                if (Objects.isNull(fileById)) {
                    throw new AppException(AppExceptionCodeMsg.BUSY);
                }
                data.setFileId((int) fileId);
                redisUtil.hincr(RedisConstants.FILE_KEY + fileMd5, "useNum", 1);
            }
            dataMapper.insert(data);
            uploadNum++;
        }

        return uploadNum;
    }


    @Override
    @Transactional
    public void delData(Integer dataId) {
        Integer userId = UserUtil.getLoginUserId();
        Data data = this.getById(dataId);
        if (Objects.isNull(data)) {
            throw new AppException(AppExceptionCodeMsg.DATA_NOT_EXISTS);
        }
        if (!data.getCreateBy().equals(userId)) {
            throw new AppException(AppExceptionCodeMsg.INVALID_PERMISSION);
        }
        deleteData(dataId, userId);
    }

    private void deleteData(Integer dataId, Integer userId) {
        this.removeById(dataId);
        recurCountDelete(dataId);
        dataDelService.insertDataDel(dataId, userId);
    }

    /**
     * 递归删除文件
     *
     * @param dataId 文件id
     */
    private void recurCountDelete(Integer dataId) {
        List<Data> dataList = this.list(new QueryWrapper<Data>().eq("parent_data_id", dataId));
        List<Integer> idList = dataList.stream().map(Data::getId).collect(Collectors.toList());
        this.removeBatchByIds(idList);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Data data : dataList) {
            if (data.getType() == DataEnum.FOLDER.getIndex()) {
                CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(
                        () -> recurCountDelete(data.getId()));
                futures.add(voidCompletableFuture);
            }
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    @Override
    public void batchDelData(List<Integer> dataIds) {
        Integer userId = UserUtil.getLoginUserId();
        CountDownLatch countDownLatch=new CountDownLatch(dataIds.size());
        for (int dataId : dataIds) {
            try {
                nowServiceThreadPool.execute(()->{
                    Data data = this.getById(dataId);
                    if (Objects.isNull(data) || !data.getCreateBy().equals(userId)) {
                        countDownLatch.countDown();
                        return;
                    }
                    deleteData(dataId, userId);
                });
            } finally {
                countDownLatch.countDown();
            }
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional
    public void finalDelData(Integer dataDelId, @Nullable String code, Integer result) {
        Integer userId = UserUtil.getLoginUserId();
        DataDel dataDel = dataDelService.getById(dataDelId);
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
        //todo rabbitmq延时队列删除
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
        dataDelService.removeById(dataDelId);
    }

    /**
     * 递归删除回收站文件
     *
     * @param data 文件
     */
    private void recurCountFinalDelete(Data data) {

        if (data.getType() != DataEnum.FOLDER.getIndex()) {
            File file = fileMapper.selectById(data.getFileId());
            if (!Objects.isNull(file)) {
                redisUtil.hdecr(RedisConstants.FILE_KEY + file.getMd5(), "useNum", 1);
                Object useNum = redisUtil.hget(RedisConstants.FILE_KEY + file.getMd5(), "useNum");
                if ((int) useNum == 0) {
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
        for (int dataDelId : dataDelIds) {
            nowServiceThreadPool.execute(() -> {
                DataDel dataDel = dataDelService.getById(dataDelId);
                if ((result != accessToFinalDelData && Objects.isNull(code))
                        || (!Objects.isNull(dataDel) && !dataDel.getCreateBy().equals(userId))
                        || Objects.isNull(dataDel)) {
                    return;
                }
                finalDeleteData(dataDelId, dataDel.getDataId(), userId);
            });
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


    @Override
    public Data updateDataName(Integer dataId, String name) {
        Integer userId = UserUtil.getLoginUserId();
        Data data = this.getById(dataId);
        if (Objects.isNull(data)) {
            throw new AppException(AppExceptionCodeMsg.DATA_NOT_EXISTS);
        }
        if (!data.getCreateBy().equals(userId)) {
            throw new AppException(AppExceptionCodeMsg.INVALID_PERMISSION);
        }
        int maxNameLength = 255;
        if (name.length() > maxNameLength) {
            throw new AppException(AppExceptionCodeMsg.DATA_NAME_TOO_LONG);
        }
        String judgeReName = judgeReName(name, data.getParentDataId(), data.getType(), userId);
        dataMapper.update(new Data(), new UpdateWrapper<Data>()
                .set("name", judgeReName)
                .eq("id", dataId));
        return this.getById(dataId);
    }


    @Override
    @Transactional
    public List<List<Data>> copyToNewFolder(List<Integer> dataIds, Integer targetFolderDataId) throws InterruptedException {
        Integer userId = UserUtil.getLoginUserId();
        if (dataIds.size() == Integer.MAX_VALUE) {
            throw new AppException(AppExceptionCodeMsg.DATA_NUM_TOO_LARGE);
        }
        Data targetFolder = dataMapper.selectOne(new QueryWrapper<Data>()
                .eq("id", targetFolderDataId)
                .eq("create_by", userId));
        //判断目标文件夹是否存在,判断完之后targetFolder必须是文件夹类型
        if ((targetFolderDataId != DataEnum.MAX_NONE_FOLDER.getIndex() && Objects.isNull(targetFolder))
                || (!Objects.isNull(targetFolder) && targetFolder.getType() != DataEnum.FOLDER.getIndex())) {
            throw new AppException(AppExceptionCodeMsg.FOLDER_NOT_EXISTS);
        }
        List<List<Data>> result = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(dataIds.size());
        //重名的文件
        List<Data> reNameList = new CopyOnWriteArrayList<>();
        //原来的文件
        List<Data> sourceList = new CopyOnWriteArrayList<>();
        List<Data> targetDataSubDataList = dataMapper.selectList(new QueryWrapper<Data>()
                .eq("parent_data_id", targetFolderDataId)
                .eq("create_by", userId));

        for (int copyDataId : dataIds) {
            Data copyData = dataMapper.selectOne(new QueryWrapper<Data>()
                    .eq("id", copyDataId)
                    .eq("create_by", userId));
            if (Objects.isNull(copyData)) {
                countDownLatch.countDown();
                continue;
            }
            //判断复制的时候是不是复制复制到原来的文件夹或错误的复制
            if (copyData.getParentDataId().equals(targetFolderDataId) || copyDataId == targetFolderDataId) {
                throw new AppException(AppExceptionCodeMsg.DATA_COPY_ERR);
            }
            nowServiceThreadPool.execute(() -> {
                try {
                    boolean shouldContinue = false;
                    //判断要复制的文件夹是否和目标文件夹内文件夹同名
                    for (Data targetDataSubData : targetDataSubDataList) {
                        if (targetDataSubData.getName().equals(copyData.getName())
                                && targetDataSubData.getType().equals(copyData.getType())) {
                            sourceList.add(targetDataSubData);
                            reNameList.add(copyData);
                            shouldContinue = true;
                            break;
                        }
                    }
                    if (!shouldContinue) {
                        Data newData = new Data(copyData.getName(), copyData.getType(), targetFolderDataId,
                                copyData.getCreateTime(), copyData.getUpdateTime(), userId, copyData.getFileId());
                        this.save(newData);
                        recurToCopy(copyData, userId, newData.getId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        result.add(reNameList);
        result.add(sourceList);
        return result;
    }

    /**
     * 递归复制
     *
     * @param copyData 要复制的文件
     * @param userId   操作的用户id
     * @param copedId  复制过的文件id
     */
    @Transactional
    void recurToCopy(Data copyData, Integer userId, Integer copedId) {
        if (copyData.getType() == DataEnum.FOLDER.getIndex()) {
            //查找要复制的文件夹所有的子文件
            List<Data> copyDataSubDataList = dataMapper.selectList(new QueryWrapper<Data>()
                    .eq("parent_data_id", copyData.getId())
                    .eq("create_by", userId));
            if (copyDataSubDataList.isEmpty()) {
                return;
            }
            for (Data copyDataSubData : copyDataSubDataList) {
                copyDataSubData.setParentDataId(copedId);
            }
            dataMapper.batchSaveData(copyDataSubDataList);
            List<Data> getSaveList = this.list(new QueryWrapper<Data>().eq("parent_data_id", copedId));
            List<Data> sourceCopyDataList = dataMapper.selectList(new QueryWrapper<Data>()
                    .eq("parent_data_id", copyData.getId())
                    .eq("create_by", userId));
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (int i = 0; i < getSaveList.size(); i++) {
                int copyDataIndex = i;
                int getSaveIndex = i;
                CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(
                        () -> recurToCopy(sourceCopyDataList.get(copyDataIndex),
                                userId, getSaveList.get(getSaveIndex).getId()));
                futures.add(voidCompletableFuture);
            }
            // 等待所有 CompletableFuture 完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } else {
            rabbitTemplate.convertAndSend(RabbitmqConstants.FILE_EXCHANGE,
                    RabbitmqConstants.BIND_ADD_FILE_MD5, copyData.getFileId());
        }
    }


    @Override
    public List<List<Data>> shearToNewFolder(List<Integer> dataIds, Integer targetFolderDataId) throws InterruptedException {
        Integer userId = UserUtil.getLoginUserId();
        int maxNoneFolderIndex = DataEnum.MAX_NONE_FOLDER.getIndex();
        if (targetFolderDataId != maxNoneFolderIndex) {
            Data fatherData = dataMapper.findByCreateByAndId(targetFolderDataId, userId);
            if (Objects.isNull(fatherData) || fatherData.getType() != DataEnum.FOLDER.getIndex()) {
                throw new AppException(AppExceptionCodeMsg.FOLDER_NOT_EXISTS);
            }
        }
        if (dataIds.size() == Integer.MAX_VALUE) {
            throw new AppException(AppExceptionCodeMsg.DATA_NUM_TOO_LARGE);
        }
        //重名的文件
        List<Data> reNameList = new CopyOnWriteArrayList<>();
        //原来的文件
        List<Data> sourceList = new CopyOnWriteArrayList<>();
        List<Data> targetDataSubDataList = dataMapper.selectList(new QueryWrapper<Data>()
                .eq("parent_data_id", targetFolderDataId)
                .eq("create_by", userId));
        List<List<Data>> result = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(dataIds.size());
        for (int shearDataId : dataIds) {
            Data shearData = this.getById(shearDataId);
            if (Objects.isNull(shearData)) {
                countDownLatch.countDown();
                continue;
            }
            if (shearData.getParentDataId().equals(targetFolderDataId)) {
                throw new AppException(AppExceptionCodeMsg.DATA_SHEAR_ERR);
            }
            nowServiceThreadPool.execute(() -> {
                Data parentData = dataMapper.findByCreateByAndId(targetFolderDataId, userId);
                //判断目标文件是否存在且必须是文件夹
                if ((targetFolderDataId != maxNoneFolderIndex && Objects.isNull(parentData))
                        || (!Objects.isNull(parentData) && parentData.getType() != DataEnum.FOLDER.getIndex())) {
                    return;
                }
                try {
                    boolean shouldContinue = false;
                    //判断要复制的文件夹是否和目标文件夹内文件夹同名
                    for (Data targetDataSubData : targetDataSubDataList) {
                        if (targetDataSubData.getName().equals(shearData.getName())
                                && targetDataSubData.getType().equals(shearData.getType())) {
                            sourceList.add(targetDataSubData);
                            reNameList.add(shearData);
                            shouldContinue = true;
                            break;
                        }
                    }
                    if (!shouldContinue) {
                        this.update(new Data(), new UpdateWrapper<Data>()
                                .set("parent_data_id", targetFolderDataId)
                                .eq("id", shearDataId));
                    }
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        result.add(reNameList);
        result.add(sourceList);
        return result;
    }

    @Override
    public void batchOverrideFiles(List<Integer> dataIds, Integer targetFolderDataId) throws InterruptedException {
        Integer userId = UserUtil.getLoginUserId();
        if (targetFolderDataId != DataEnum.MAX_NONE_FOLDER.getIndex()) {
            Data fatherData = dataMapper.findByCreateByAndId(targetFolderDataId, userId);
            if (Objects.isNull(fatherData) || fatherData.getType() != DataEnum.FOLDER.getIndex()) {
                throw new AppException(AppExceptionCodeMsg.FOLDER_NOT_EXISTS);
            }
        }
        if (dataIds.size() == Integer.MAX_VALUE) {
            throw new AppException(AppExceptionCodeMsg.DATA_NUM_TOO_LARGE);
        }
        CountDownLatch countDownLatch = new CountDownLatch(dataIds.size());
        for (int overrideDataId : dataIds) {
            Data copyData = this.getById(overrideDataId);
            if (Objects.isNull(copyData)) {
                continue;
            }
            nowServiceThreadPool.execute(() -> {
                try {
//                    recurToCopy(overrideDataId, targetFolderDataId, userId);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
    }


    @Override
    public void batchGenerateDuplicates(List<Integer> ids, Integer newDataId) {

    }

    @Override
    public void addToQuickAccess(Set<Integer> dataIds) {

    }

    @Override
    @Transactional
    public void restoreData(List<Integer> dataDelIds) {
        Integer userId = UserUtil.getLoginUserId();
        for (Integer dataDelId : dataDelIds) {
            DataDel dataDel = dataDelService.getById(dataDelId);
            if (Objects.isNull(dataDel)) {
                continue;
            }
            dataMapper.restoreDeleteData(dataDel.getDataId(), userId);
            recurToRestoreData(dataDel.getDataId(), userId);
            dataDelService.removeById(dataDel.getId());
        }
    }

    /**
     * 递归还原被删除文件
     *
     * @param dataId Integer 文件id
     * @param userId Integer 用户id
     */
    private void recurToRestoreData(Integer dataId, Integer userId) {
        List<Data> deleteDataList = dataMapper.findDeleteListByCreateByAndParentId(dataId, userId);
        dataMapper.batchRestoreData(dataId, userId);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Data deleteData : deleteDataList) {
            if (deleteData.getType() == 0) {
                CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(
                        () ->  recurToRestoreData(deleteData.getId(), userId));
                futures.add(voidCompletableFuture);
            }
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    /**
     * 递归计算所有文件大小
     *
     * @param dataId    文件id
     * @param recurInfo RecurInfo
     */
    private void recurCountSize(Integer dataId, RecurInfo recurInfo) {
        List<Data> dataList = this.list(new QueryWrapper<Data>().eq("parent_data_id", dataId));
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Data data : dataList) {
            if (data.getType() == DataEnum.FOLDER.getIndex()) {
                recurInfo.folderNum++;
                CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(
                        () ->  recurCountSize(data.getId(), recurInfo));
                futures.add(voidCompletableFuture);
            } else {
                Integer fileId = this.getById(data.getId()).getFileId();
                File file = fileMapper.selectById(fileId);
                if (!Objects.isNull(file)) {
                    recurInfo.dataSize += Long.parseLong(file.getBytes());
                }
                recurInfo.fileNum++;
            }
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private Data createData(MultipartFile file, Integer parentDataId) {
        Integer userId = UserUtil.getLoginUserId();
        Data data = new Data();
        String name = judgeReName(file.getOriginalFilename(), parentDataId, 1, userId);
        int fileType = UploadUtil.getFileType(file);
        //如果当前目录不为根目录
        if (parentDataId != 0) {
            Data parentData = this.getById(parentDataId);
            //如果当前目录不存在
            if (Objects.isNull(parentData)) {
                return null;
            }
            data.setParentDataId(parentDataId);
        } else {
            data.setParentDataId(0);
        }
        data.setName(name);
        data.setCreateBy(userId);
        data.setCreateTime(new Date());
        data.setType(fileType);
        return data;
    }


    private File createFile(String md5, String path, Integer userId, String size, String bytes) {
        File newFile = new File();
        newFile.setMd5(md5);
        newFile.setLink(path);
        newFile.setCreateTime(new Date());
        newFile.setCreateBy(userId);
        newFile.setSize(size);
        newFile.setBytes(bytes);
        return newFile;
    }

    /**
     * 根据字节值来计算文件大小
     *
     * @param sizeInBytes 文件字节大小
     * @return String
     */
    public String getFileSize(long sizeInBytes) {
        double sizeInKb = sizeInBytes / 1024.0;
        if (sizeInKb < 1) {
            return sizeInBytes + " B";
        }
        double sizeInMb = sizeInKb / 1024.0;
        if (sizeInMb < 1) {
            return String.format("%.2f KB", sizeInKb);
        }
        double sizeInGb = sizeInMb / 1024.0;
        if (sizeInGb < 1) {
            return String.format("%.2f MB", sizeInMb);
        }
        return String.format("%.2f GB", sizeInGb);
    }


    /**
     * 将文件存储redis里面
     *
     * @param md5      String 计算出来的文件MD5值
     * @param fileId   Integer 真实文件id
     * @param fileSize long 真实文件大小
     */
    public void redisStorageFile(String md5, Integer fileId, long fileSize) {
        String hashKey = RedisConstants.FILE_KEY + md5;
        //todo 添加分布式锁,这里会出现并发安全问题
        redisUtil.hset(hashKey, "fileId", fileId);
        redisUtil.hset(hashKey, "fileSize", fileSize);
        redisUtil.hset(hashKey, "useNum", 1);
    }


    /**
     * 判断是否重名并返回修改后的名字
     *
     * @param name         名字
     * @param parentDataId 父文件id
     * @return String
     */
    private String judgeReName(String name, Integer parentDataId, Integer type, Integer userId) {
        StringBuilder stringBuilder = new StringBuilder();
        if (type == 0) {
            List<Data> dataList = this.list(new QueryWrapper<Data>()
                    .eq("parent_data_id", parentDataId)
                    .eq("type", DataEnum.FOLDER.getIndex())
                    .eq("create_by", userId));
            for (Data data : dataList) {
                if (data.getName().equals(name)) {
                    stringBuilder.append(name).append("_").append(RandomUtil.randomNumbers(8));
                    return stringBuilder.toString();
                }
            }
        } else {
            List<Data> dataList = this.list(new QueryWrapper<Data>()
                    .eq("parent_data_id", parentDataId)
                    .ne("type", DataEnum.FOLDER.getIndex())
                    .eq("create_by", userId));
            for (Data data : dataList) {
                if (data.getName().equals(name)) {
                    String str = name.split("\\.")[0];
                    String last = name.split("\\.")[1];
                    stringBuilder.append(str).append("_").append(RandomUtil.randomNumbers(8)).append(".").append(last);
                    return stringBuilder.toString();
                }
            }
        }
        return name;
    }


    @lombok.Data
    @AllArgsConstructor

    static class RecurInfo {
        private Integer folderNum;
        private Integer fileNum;
        private long dataSize;
    }


    @Override
    protected void finalize() throws Throwable {
        try {
            // 在对象被垃圾回收前确保线程池被关闭
            closeThreadPool();
        } finally {
            super.finalize();
        }
    }

    private void closeThreadPool() {
        nowServiceThreadPool.shutdown();
        recurHelpThreadPool.shutdown();
    }


}




