package com.netdisk.web.sysController;

import com.netdisk.common.enums.AppExceptionCodeMsg;
import com.netdisk.common.exception.AppException;
import com.netdisk.common.core.ResponseResult;
import com.netdisk.system.pojo.Data;
import com.netdisk.system.service.QuickDataService;
import com.netdisk.web.tool.GeneralRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author lsj
 */
@Api(value = "快速访问接口")
@RestController
@RequestMapping("/sysData")
public class QuickDataController {

    @Autowired
    QuickDataService quickDataService;

    @ApiOperation(value = "遍历快捷访问")
    @GetMapping("/traverseQuickAccess")
    public ResponseResult traverseQuickAccess() {
        List<Data> dataList = quickDataService.getQuickDataIdList();
        return ResponseResult.success(dataList);
    }


    @ApiOperation(value = "添加文件到快捷访问")
    @PostMapping("/addToQuickAccess")
    public ResponseResult addToQuickAccess(@RequestBody GeneralRequest generalRequest) {
        Set<Integer> ids = generalRequest.getIds();
        if (ids.isEmpty()) {
            throw new AppException(AppExceptionCodeMsg.NULL_VALUE);
        }
        List<Integer> dataIds = new ArrayList<>(ids);
        boolean b = quickDataService.addQuickAccess(dataIds);
        return b ? ResponseResult.success() : ResponseResult.error(AppExceptionCodeMsg.BUSY);
    }

    @ApiOperation(value = "删除快捷访问")
    @DeleteMapping("/delQuickAccess/{quickId}")
    public ResponseResult delQuickAccess(@PathVariable Integer quickId) {
        if (Objects.isNull(quickId)) {
            throw new AppException(AppExceptionCodeMsg.NULL_VALUE);
        }
        boolean b = quickDataService.delQuickAccess(quickId);
        return b ? ResponseResult.success() : ResponseResult.error(AppExceptionCodeMsg.BUSY);
    }
}
