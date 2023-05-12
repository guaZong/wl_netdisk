package com.sk.netdisk.controller;

import cn.hutool.core.util.ObjectUtil;
import com.sk.netdisk.enums.AppExceptionCodeMsg;
import com.sk.netdisk.enums.DataEnum;
import com.sk.netdisk.exception.AppException;
import com.sk.netdisk.pojo.Data;
import com.sk.netdisk.pojo.dto.DataDetInfoDto;
import com.sk.netdisk.pojo.dto.DataPathDto;
import com.sk.netdisk.pojo.vo.DataDelInfoVo;
import com.sk.netdisk.pojo.vo.DataInfoVo;
import com.sk.netdisk.service.DataService;
import com.sk.netdisk.util.ResponseResult;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Administrator
 */
@RestController
@CrossOrigin
@RequestMapping("/sysData")
public class DataController {

    DataService dataService;

    @Autowired
    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    @ApiOperation(value = "遍历文件")
    @GetMapping("/infoData")
    public ResponseResult infoData(Integer parentDataId) {
        if (Objects.isNull(parentDataId)) {
            throw new AppException(AppExceptionCodeMsg.FOLDER_NOT_EXISTS);
        }
        List<DataDetInfoDto> dataList = dataService.infoData(parentDataId);
        return ResponseResult.success(dataList);
    }


    @ApiOperation(value = "创建文件夹")
    @PostMapping("/createFolder")
    public ResponseResult createFolder(Integer parentDataId, String folderName) {
        if (Objects.isNull(parentDataId)) {
            throw new AppException(AppExceptionCodeMsg.FOLDER_NOT_EXISTS);
        }
        if (Objects.isNull(folderName)) {
            throw new AppException(AppExceptionCodeMsg.NAME_IS_NULL);
        }
        Data newFolder = dataService.createFolder(parentDataId, folderName);
        return ResponseResult.success(newFolder);
    }


    @ApiOperation(value = "上传文件")
    @PostMapping("/uploadData")
    public ResponseResult uploadData(MultipartFile[] files, Integer parentDataId) throws Exception {
        if (Objects.isNull(parentDataId)) {
            throw new AppException(AppExceptionCodeMsg.DATA_NOT_EXISTS);
        }
        if (ObjectUtil.isEmpty(files)) {
            throw new AppException(AppExceptionCodeMsg.UPLOAD_FILE_IS_NULL);
        }
        Integer uploadNum = dataService.uploadMinData(files, parentDataId);
        if (uploadNum > 0) {
            return ResponseResult.success();
        }
        return ResponseResult.error("上传失败");
    }

    @ApiOperation(value = "获取文件详细信息")
    @GetMapping("/getDataInfo")
    public ResponseResult getDataInfo(Integer dataId) {
        if (Objects.isNull(dataId)) {
            throw new AppException(AppExceptionCodeMsg.DATA_NOT_EXISTS);
        }
        DataInfoVo dataInfo = dataService.getDataInfo(dataId);
        return ResponseResult.success(dataInfo);
    }


    @ApiOperation(value = "修改文件名称")
    @PutMapping("/updateDataName")
    public ResponseResult updateDataName(Integer dataId, String name) {
        if (Objects.isNull(dataId)) {
            throw new AppException(AppExceptionCodeMsg.DATA_NOT_EXISTS);
        }
        if (StringUtils.isEmpty(name)) {
            throw new AppException(AppExceptionCodeMsg.NAME_IS_NULL);
        }
        Data data = dataService.updateDataName(dataId, name);
        return ResponseResult.success(data);
    }


    @ApiOperation(value = "删除文件")
    @DeleteMapping("/delData")
    public ResponseResult delData(Integer dataId) {
        if (Objects.isNull(dataId)) {
            throw new AppException(AppExceptionCodeMsg.DATA_NOT_EXISTS);
        }
        dataService.delData(dataId);
        return ResponseResult.success();
    }

    @ApiOperation(value = "遍历回收站文件")
    @GetMapping("/infoDataDel")
    public ResponseResult infoDataDel() {
        List<DataDelInfoVo> dataDelInfoVos = dataService.infoDataDel();
        return ResponseResult.success(dataDelInfoVos);
    }

    @ApiOperation(value = "返回目前文件的父文件夹id")
    @GetMapping("/getParentDataId")
    public ResponseResult getParentDataId(Integer nowDataId) {
        Integer parentDataId = dataService.getParentDataId(nowDataId);
        return ResponseResult.success(parentDataId);
    }

    @ApiOperation(value = "返回文件目录")
    @GetMapping("/getDataFolder")
    public ResponseResult getDataFolder(Integer folderId) {
        if (Objects.isNull(folderId)) {
            throw new AppException(AppExceptionCodeMsg.FOLDER_NOT_EXISTS);
        }
        List<Data> folderList = dataService.getDataFolder(folderId);
        return ResponseResult.success(folderList);
    }


    @ApiOperation(value = "删除回收站文件--彻底删除")
    @DeleteMapping("/finalDelData")
    public ResponseResult finalDelData(Integer dataDelId, String code) {
        if (Objects.isNull(dataDelId)) {
            throw new AppException(AppExceptionCodeMsg.DATA_NOT_EXISTS);
        }
        Integer result = dataService.judgeSendDelCode();
        if (result != DataEnum.ACCESS_TO_FINAL_DEL.getIndex() && StringUtils.isEmpty(code)) {
            return ResponseResult.success(result);
        }
        code = Objects.isNull(code) ? "" : code;
        dataService.finalDelData(dataDelId, code, result);
        return ResponseResult.success();
    }


    @ApiOperation(value = "复制文件到另一个文件夹--复制操作")
    @PostMapping("/copyToNewFolder")
    public ResponseResult copyToNewFolder(@RequestParam("dataIds") Set<Integer> dataIds, Integer parentDataId) throws InterruptedException {
        if (dataIds.isEmpty() || Objects.isNull(parentDataId)) {
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        List<Integer> ids = new ArrayList<>(dataIds);
        List<List<Data>> dataList = dataService.copyToNewFolder(ids, parentDataId);
        if (dataList.get(0).isEmpty()) {
            return ResponseResult.success();
        }
        return ResponseResult.error(AppExceptionCodeMsg.DATA_RENAME, dataList);
    }

    @ApiOperation(value = "移动文件到另一个文件夹--剪切操作")
    @PostMapping("/shearToNewFolder")
    public ResponseResult shearToNewFolder(@RequestParam("dataIds") Set<Integer> dataIds, Integer newDataId) throws InterruptedException {
        if (dataIds.isEmpty() || newDataId == null) {
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        List<Integer> ids = new ArrayList<>(dataIds);
        List<List<Data>> dataList = dataService.shearToNewFolder(ids, newDataId);
        if (dataList.get(0).isEmpty()) {
            return ResponseResult.success();
        }
        return ResponseResult.error(AppExceptionCodeMsg.DATA_RENAME, dataList);
    }

    @ApiOperation(value = "批量覆盖原有文件")
    @PostMapping("/batchOverrideFiles")
    public ResponseResult batchOverrideFiles(@RequestParam("dataIds") Set<Integer> dataIds, Integer newDataId) throws InterruptedException {
        if (dataIds.isEmpty() || newDataId == null) {
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        List<Integer> ids = new ArrayList<>(dataIds);
        dataService.batchOverrideFiles(ids, newDataId);
        return ResponseResult.success();
    }

    @ApiOperation(value = "批量生成副本")
    @PostMapping("/batchGenerateDuplicates")
    public ResponseResult batchGenerateDuplicates(@RequestParam("dataIds") Set<Integer> dataIds, Integer newDataId) throws InterruptedException {
        if (dataIds.isEmpty() || newDataId == null) {
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        List<Integer> ids = new ArrayList<>(dataIds);
        dataService.batchGenerateDuplicates(ids, newDataId);
        return ResponseResult.success();
    }

    @ApiOperation(value = "添加文件到快捷访问")
    @PostMapping("/addToQuickAccess")
    public ResponseResult addToQuickAccess(@RequestParam("dataIds") Set<Integer> dataIds) {
        dataService.addToQuickAccess(dataIds);
        return ResponseResult.success();
    }


    @ApiOperation(value = "将回收站文件还原")
    @PostMapping("/restoreData")
    public ResponseResult restoreData(@RequestParam("dataDelIds") List<Integer> dataDelIds) {
        if (dataDelIds.isEmpty()) {
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        dataService.restoreData(dataDelIds);
        return ResponseResult.success();
    }

    @ApiOperation(value = "批量删除文件")
    @DeleteMapping("/batchDelData")
    public ResponseResult batchDelData(@RequestParam("dataIds") Set<Integer> dataIds) {
        if (dataIds.isEmpty()) {
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        List<Integer> ids = new ArrayList<>(dataIds);
        dataService.batchDelData(ids);
        return ResponseResult.success();
    }


    @ApiOperation(value = "批量删除文件")
    @DeleteMapping("/batchFinalDelData")
    public ResponseResult batchFinalDelData(@RequestParam("dataDelIds") Set<Integer> dataDelIds, String code) {
        if (dataDelIds.isEmpty()) {
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        Integer result = dataService.judgeSendDelCode();
        if (result != DataEnum.ACCESS_TO_FINAL_DEL.getIndex() && Objects.isNull(code)) {
            return ResponseResult.success(result);
        }
        code = Objects.isNull(code) ? "" : code;
        List<Integer> ids = new ArrayList<>(dataDelIds);
        dataService.batchFinalDelData(ids, code, result);
        return ResponseResult.success();
    }


    @ApiOperation(value = "遍历快捷访问")
    @GetMapping("/traverseQuickAccess")
    public ResponseResult traverseQuickAccess() {
       List<Data> dataList= dataService.traverseQuickAccess();
        return ResponseResult.success();
    }

    @ApiOperation(value = "获取路径")
    @GetMapping("/getDataPath")
    public ResponseResult getDataPath(Integer dataId) {
        if(Objects.isNull(dataId)){
            throw new AppException(AppExceptionCodeMsg.DATA_NOT_EXISTS);
        }
        List<DataPathDto> dataPath = dataService.getDataPath(dataId);
        return ResponseResult.success(dataPath);
    }

}
