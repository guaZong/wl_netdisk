package com.netdisk.web.sysController;

import cn.hutool.core.util.ObjectUtil;
import com.netdisk.common.enums.AppExceptionCodeMsg;
import com.netdisk.common.enums.DataEnum;
import com.netdisk.common.exception.AppException;
import com.netdisk.common.core.ResponseResult;
import com.netdisk.common.utils.UserUtil;
import com.netdisk.system.dto.DataDetInfoDto;
import com.netdisk.system.dto.DataPathDto;
import com.netdisk.system.pojo.Data;
import com.netdisk.system.pojo.vo.DataDelInfoVo;
import com.netdisk.system.pojo.vo.DataInfoVo;
import com.netdisk.system.service.DataDelService;
import com.netdisk.system.service.DataService;
import com.netdisk.web.tool.GeneralRequest;
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
 * @author lsj
 */
@RestController
@CrossOrigin
@RequestMapping("/sysData")
public class DataController {

    DataService dataService;
    DataDelService dataDelService;

    @Autowired
    public DataController(DataService dataService, DataDelService dataDelService) {
        this.dataService = dataService;
        this.dataDelService = dataDelService;
    }

    @ApiOperation(value = "遍历文件")
    @GetMapping("/infoData/{parentDataId}")
    public ResponseResult infoData(@PathVariable Integer parentDataId) {
        if (Objects.isNull(parentDataId)) {
            throw new AppException(AppExceptionCodeMsg.FOLDER_NOT_EXISTS);
        }
        List<DataDetInfoDto> dataList = dataService.traverseDataByParentId(parentDataId);
        return ResponseResult.success(dataList);
    }

    @ApiOperation(value = "根据类型遍历")
    @GetMapping("/traverseDataByType/{type}")
    public ResponseResult traverseDataByType(@PathVariable Integer type) {
        if (Objects.isNull(type)) {
            throw new AppException(AppExceptionCodeMsg.NULL_VALUE);
        }
        List<DataDetInfoDto> dataList = dataService.traverseDataByType(type);
        return ResponseResult.success(dataList);
    }


    @ApiOperation(value = "创建文件夹")
    @PostMapping("/createFolder")
    public ResponseResult createFolder(@RequestBody Data data) {
        Integer parentDataId = data.getParentDataId();
        String folderName = data.getName();
        if (Objects.isNull(parentDataId)) {
            throw new AppException(AppExceptionCodeMsg.FOLDER_NOT_EXISTS);
        }
        if (StringUtils.isEmpty(folderName)) {
            throw new AppException(AppExceptionCodeMsg.NAME_IS_NULL);
        }
        Data newFolder = dataService.createFolder(parentDataId, folderName.trim());
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
    @GetMapping("/getDataInfo/{dataId}")
    public ResponseResult getDataInfo(@PathVariable Integer dataId) {
        if (Objects.isNull(dataId)) {
            throw new AppException(AppExceptionCodeMsg.DATA_NOT_EXISTS);
        }
        DataInfoVo dataInfo = dataService.getDataDetail(dataId);
        return ResponseResult.success(dataInfo);
    }


    @ApiOperation(value = "修改文件名称")
    @PutMapping("/updateDataName")
    public ResponseResult updateDataName(@RequestBody Data data) {
        Integer dataId = data.getId();
        String name = data.getName();
        if (Objects.isNull(dataId)) {
            throw new AppException(AppExceptionCodeMsg.DATA_NOT_EXISTS);
        }
        if (StringUtils.isEmpty(name)) {
            throw new AppException(AppExceptionCodeMsg.NAME_IS_NULL);
        }
        return ResponseResult.success(dataService.updateDataName(dataId, name.trim()));
    }


    @ApiOperation(value = "删除文件")
    @DeleteMapping("/delData/{dataId}")
    public ResponseResult delData(@PathVariable Integer dataId) {
        if (Objects.isNull(dataId)) {
            throw new AppException(AppExceptionCodeMsg.DATA_NOT_EXISTS);
        }
        dataService.delData(dataId);
        return ResponseResult.success();
    }

    @ApiOperation(value = "遍历回收站文件")
    @GetMapping("/infoDataDel")
    public ResponseResult infoDataDel() {
        Integer userId = UserUtil.getLoginUserId();
        List<DataDelInfoVo> dataDelInfoVos = dataDelService.infoAllDataDel(userId);
        return ResponseResult.success(dataDelInfoVos);
    }

    @ApiOperation(value = "返回目前文件的父文件夹id")
    @GetMapping("/getParentDataId/{nowDataId}")
    public ResponseResult getParentDataId(@PathVariable Integer nowDataId) {
        Integer parentDataId = dataService.getParentDataId(nowDataId);
        return ResponseResult.success(parentDataId);
    }

    @ApiOperation(value = "返回文件目录")
    @GetMapping("/getDataFolder/{folderId}")
    public ResponseResult getDataFolder(@PathVariable Integer folderId) {
        if (Objects.isNull(folderId)) {
            throw new AppException(AppExceptionCodeMsg.FOLDER_NOT_EXISTS);
        }
        List<Data> folderList = dataService.getDataFolder(folderId);
        return ResponseResult.success(folderList);
    }


    @ApiOperation(value = "删除回收站文件--彻底删除")
    @DeleteMapping("/finalDelData/{dataDelId}")
    public ResponseResult finalDelData(@PathVariable Integer dataDelId, @RequestParam String code) {
        if (Objects.isNull(dataDelId)) {
            throw new AppException(AppExceptionCodeMsg.DATA_NOT_EXISTS);
        }
        Integer result = dataDelService.judgeSendDelCode();
        if (result != DataEnum.ACCESS_TO_FINAL_DEL.getIndex() && StringUtils.isEmpty(code)) {
            return ResponseResult.success(result);
        }
        code = Objects.isNull(code) ? "" : code;
        dataDelService.finalDelData(dataDelId, code, result);
        return ResponseResult.success();
    }


    @ApiOperation(value = "复制文件到另一个文件夹--复制操作")
    @PostMapping("/copyToNewFolder")
    public ResponseResult copyToNewFolder(@RequestBody GeneralRequest generalRequest) {
        Set<Integer> dataIds = generalRequest.getIds();
        Integer newDataId = generalRequest.getTargetFolderId();
        if (dataIds.isEmpty() || Objects.isNull(newDataId)) {
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        List<Integer> ids = new ArrayList<>(dataIds);
        List<List<Data>> dataList = dataService.copyToNewFolder(ids, newDataId);
        if (dataList.get(0).isEmpty()) {
            return ResponseResult.success();
        }
        return ResponseResult.error(AppExceptionCodeMsg.DATA_RENAME, dataList);
    }

    @ApiOperation(value = "移动文件到另一个文件夹--剪切操作")
    @PostMapping("/shearToNewFolder")
    public ResponseResult shearToNewFolder(@RequestBody GeneralRequest generalRequest) throws InterruptedException {
        Set<Integer> dataIds = generalRequest.getIds();
        Integer newDataId = generalRequest.getTargetFolderId();
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
    public ResponseResult batchOverrideFiles(@RequestBody GeneralRequest generalRequest) {
        Set<Integer> sourceDataIds = generalRequest.getSids();
        Set<Integer> dataIds = generalRequest.getIds();
        Integer newDataId = generalRequest.getTargetFolderId();
        Integer auto = 0;
        if (dataIds.isEmpty() || newDataId == null || sourceDataIds.isEmpty() || dataIds.size() != sourceDataIds.size()) {
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        List<Integer> ids = new ArrayList<>(dataIds);
        List<Integer> sourceIds = new ArrayList<>(sourceDataIds);
        dataService.batchOverrideFiles(ids, newDataId, sourceIds, auto);
        return ResponseResult.success();
    }

    @ApiOperation(value = "批量生成副本")
    @PostMapping("/batchGenerateDuplicates")
    public ResponseResult batchGenerateDuplicates(@RequestBody GeneralRequest generalRequest) throws InterruptedException {
        Set<Integer> dataIds = generalRequest.getIds();
        Integer newDataId = generalRequest.getTargetFolderId();
        if (dataIds.isEmpty() || newDataId == null) {
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        List<Integer> ids = new ArrayList<>(dataIds);
        dataService.batchGenerateDuplicates(ids, newDataId);
        return ResponseResult.success();
    }


    @ApiOperation(value = "将回收站文件还原")
    @PostMapping("/restoreData")
    public ResponseResult restoreData(@RequestBody GeneralRequest generalRequest) {
        Set<Integer> ids = generalRequest.getIds();
        List<Integer> dataDelIds = new ArrayList<>(ids);
        if (dataDelIds.isEmpty()) {
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        dataService.restoreData(dataDelIds);
        return ResponseResult.success();
    }

    @ApiOperation(value = "批量删除文件")
    @DeleteMapping("/batchDelData")
    public ResponseResult batchDelData(@RequestBody GeneralRequest generalRequest) {
        Set<Integer> dataDelIds = generalRequest.getIds();
        if (dataDelIds.isEmpty()) {
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        List<Integer> ids = new ArrayList<>(dataDelIds);
        dataService.batchDelData(ids);
        return ResponseResult.success();
    }


    @ApiOperation(value = "批量删除回收站文件")
    @DeleteMapping("/batchFinalDelData")
    public ResponseResult batchFinalDelData(@RequestBody GeneralRequest generalRequest) {
        Set<Integer> dataDelIds = generalRequest.getIds();
        String code = generalRequest.getCode();
        if (dataDelIds.isEmpty()) {
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        Integer result = dataDelService.judgeSendDelCode();
        if (result != DataEnum.ACCESS_TO_FINAL_DEL.getIndex() && Objects.isNull(code)) {
            return ResponseResult.success(result);
        }
        code = Objects.isNull(code) ? "" : code;
        List<Integer> ids = new ArrayList<>(dataDelIds);
        dataDelService.batchFinalDelData(ids, code, result);
        return ResponseResult.success();
    }

    @ApiOperation(value = "获取路径")
    @GetMapping("/getDataPath/{dataId}")
    public ResponseResult getDataPath(@PathVariable Integer dataId) {
        if (Objects.isNull(dataId)) {
            throw new AppException(AppExceptionCodeMsg.DATA_NOT_EXISTS);
        }
        List<DataPathDto> dataPath = dataService.getDataPath(dataId);
        return ResponseResult.success(dataPath);
    }

    @ApiOperation(value = "修改文件排序规则")
    @PostMapping("/setSortNum")
    public ResponseResult setSortNum(@RequestBody GeneralRequest generalRequest) {
        Integer sortType = generalRequest.getSortType();
        Integer sortOrder = generalRequest.getSortOrder();
        if (Objects.isNull(sortType) || Objects.isNull(sortOrder)) {
            throw new AppException(AppExceptionCodeMsg.NULL_VALUE);
        }
        dataService.setSortNum(sortType, sortOrder);
        return ResponseResult.success();
    }


    @ApiOperation(value = "获取当前文件排序规则")
    @GetMapping("/getSortNum")
    public ResponseResult getSortNum() {
        List<Integer> sortNum = dataService.getSortNum();
        return ResponseResult.success(sortNum);
    }



}
