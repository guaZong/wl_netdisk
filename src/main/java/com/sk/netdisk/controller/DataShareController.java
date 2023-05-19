package com.sk.netdisk.controller;

import com.sk.netdisk.controller.request.GeneralRequest;
import com.sk.netdisk.enums.AppExceptionCodeMsg;
import com.sk.netdisk.exception.AppException;
import com.sk.netdisk.pojo.Data;
import com.sk.netdisk.pojo.DataShare;
import com.sk.netdisk.pojo.dto.ShareInfoDto;
import com.sk.netdisk.service.DataShareService;
import com.sk.netdisk.util.ResponseResult;
import com.sk.netdisk.util.UserUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 文件分享
 *
 * @author lsj
 */
@RestController
@RequestMapping("/sysShare")
public class DataShareController {

    final private DataShareService dataShareService;

    @Autowired
    public DataShareController(DataShareService dataShareService) {
        this.dataShareService = dataShareService;
    }

    @ApiOperation(value = "创建分享文件")
    @PostMapping("/createShareFile")
    public ResponseResult createShareFile(@RequestBody DataShare dataShare) {
        Set<Integer> ids = dataShare.getDataIds();
        String passCode = dataShare.getPassCode();
        Integer accessNum = dataShare.getAccessNum();
        Integer accessStatus = dataShare.getAccessStatus();
        Integer expireDays = dataShare.getExpireDays();
        System.out.println(ids);
        System.out.println(accessStatus);
        System.out.println(expireDays);
        if (ids.isEmpty() || Objects.isNull(accessStatus) || Objects.isNull(expireDays)) {
            throw new AppException(AppExceptionCodeMsg.NULL_VALUE);
        }
        List<Integer> dataIds = new ArrayList<>(ids);
        DataShare shareFile = dataShareService.createShareFile(dataIds, passCode, accessNum, accessStatus, expireDays);
        return ResponseResult.success(shareFile);
    }


    @ApiOperation(value = "取消分享文件")
    @DeleteMapping("/cancelShare/{shareId}")
    public ResponseResult cancelShare(@PathVariable Integer shareId) {
        if (Objects.isNull(shareId)) {
            throw new AppException(AppExceptionCodeMsg.NULL_VALUE);
        }
        Integer userId = UserUtil.getLoginUserId();
        dataShareService.cancelShare(shareId, userId);
        return ResponseResult.success();
    }

    @ApiOperation(value = "批量取消分享文件")
    @DeleteMapping("/batchCancelShare")
    public ResponseResult batchCancelShare(@RequestBody GeneralRequest generalRequest) {
        Set<Integer> ids = generalRequest.getIds();
        if (ids.isEmpty()) {
            throw new AppException(AppExceptionCodeMsg.NULL_VALUE);
        }
        List<Integer> shareIds = new ArrayList<>(ids);
        dataShareService.batchCancelShare(shareIds);
        return ResponseResult.success();
    }

    @ApiOperation(value = "遍历我的分享")
    @GetMapping("/traverseShares")
    public ResponseResult traverseShares() {
        List<ShareInfoDto> shareInfoDtoList = dataShareService.traverseShares();
        return ResponseResult.success(shareInfoDtoList);
    }

    @ApiOperation(value = "将分享文件保存至我的资源")
    @PostMapping("/saveToMyResource")
    public ResponseResult saveToMyResource(@RequestBody GeneralRequest generalRequest) {
        Set<Integer> ids = generalRequest.getIds();
        Integer shareId = generalRequest.getShareId();
        Integer targetFolderId = generalRequest.getTargetFolderId();
        String code = generalRequest.getCode();
        code = Objects.isNull(code) ? "" : code;
        if (ids.isEmpty() || Objects.isNull(shareId) || Objects.isNull(targetFolderId)) {
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        List<Integer> dataIds = new ArrayList<>(ids);
        List<List<Data>> result = dataShareService.saveToMyResource(dataIds, shareId, targetFolderId, code);
        if (result.get(0).isEmpty()) {
            return ResponseResult.success();
        }
        return ResponseResult.error(AppExceptionCodeMsg.DATA_RENAME, result);
    }


}
