package com.netdisk.web.sysController;

import com.netdisk.common.enums.AppExceptionCodeMsg;
import com.netdisk.common.enums.DataEnum;
import com.netdisk.common.exception.AppException;
import com.netdisk.common.core.ResponseResult;
import com.netdisk.system.dto.DataDetInfoDto;
import com.netdisk.system.dto.DataPathDto;
import com.netdisk.system.service.DataService;
import com.netdisk.system.service.DataShareService;
import com.netdisk.system.service.UserService;
import com.netdisk.common.utils.Regex.RegexUtils;
import com.netdisk.web.tool.GeneralRequest;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;


/**
 * 无需认证API
 *
 * @author lsj
 */
@RestController
@RequestMapping("/sysVisitor")
public class BaseController {

    private final UserService userService;

    private final DataService dataService;

    private final DataShareService dataShareService;

    public BaseController(UserService userService,DataService dataService, DataShareService dataShareService) {
        this.userService = userService;
        this.dataService = dataService;
        this.dataShareService = dataShareService;
    }


    @ApiOperation(value = "根据手机号发送登录注册验证码")
    @GetMapping("/sendCode")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "username", value = "手机号码", required = true)
            }
    )
    public ResponseResult sendCode(String username) {
        if (StringUtils.isAnyBlank(username)) {
            throw new AppException(AppExceptionCodeMsg.PHONE_IS_NULL);
        }
        String code = userService.sendPhoneCode(username);
        return ResponseResult.success(code);
    }

    @ApiOperation(value = "发送通过手机找回密码的验证码")
    @GetMapping("/findPwd/sendPCode")
    public ResponseResult sendFindPwdPhoneCode(String phoneNumber) {
        String code = userService.sendFindPwdPhoneCode(phoneNumber);
        return ResponseResult.success(code);
    }


    @ApiOperation(value = "通过手机验证码找回密码")
    @PostMapping("/findPwd/pSetNewPwd")
    public ResponseResult pSetNewPwd(@RequestBody GeneralRequest generalRequest) {
        String phoneNumber = generalRequest.getPhoneNumber();
        String password = generalRequest.getNewPassword();
        String rePassword = generalRequest.getRePassword();
        String code = generalRequest.getCode();
        if (StringUtils.isAnyBlank(password, rePassword, phoneNumber, code)) {
            throw new AppException(AppExceptionCodeMsg.NULL_VALUE);
        }
        if (!RegexUtils.isPhoneInvalid(phoneNumber)) {
            throw new AppException(AppExceptionCodeMsg.PHONE_FORMAT_INVALID);
        }
        if (!password.equals(rePassword)) {
            throw new AppException(AppExceptionCodeMsg.RE_PASSWORD_INVALID);
        }
        return userService.pSetNewPwd(phoneNumber, code, password, rePassword);
    }


    @ApiOperation(value = "发送删除回收站的验证码")
    @GetMapping("/sendFinalDelCode")
    public ResponseResult sendFinalDelCode(String phoneNumber) {
        if (StringUtils.isAnyBlank(phoneNumber)) {
            throw new AppException(AppExceptionCodeMsg.NULL_VALUE);
        }
        if (!RegexUtils.isPhoneInvalid(phoneNumber)) {
            throw new AppException(AppExceptionCodeMsg.PHONE_FORMAT_INVALID);
        }
        String code = userService.senFinalDelCode(phoneNumber);
        return ResponseResult.success(code);
    }

    @ApiOperation(value = "验证码登录")
    @PostMapping("/clogin")
    public void clogin() {
    }

    @ApiOperation(value = "密码登录")
    @PostMapping("/plogin")
    public void plogin() {

    }


    @ApiOperation(value = "遍历某个被分享的文件")
    @GetMapping("/getShareData")
    public ResponseResult getShareData(String link,String passCode) {
        if (StringUtils.isEmpty(link)) {
            throw new AppException(AppExceptionCodeMsg.NULL_VALUE);
        }
        List<DataDetInfoDto> dataList = dataShareService.getShareData(link, passCode);
        Integer shareId = dataShareService.findIdByUidAndCode(link, passCode);
        return ResponseResult.success("操作成功",dataList,shareId);
    }


    @ApiOperation(value = "无权限遍历分享的文件")
    @GetMapping("/infoData/{parentDataId}")
    public ResponseResult infoData(@PathVariable Integer parentDataId,Integer shareId,String passCode) {
        if (Objects.isNull(parentDataId) || Objects.isNull(shareId)) {
            throw new AppException(AppExceptionCodeMsg.FOLDER_NOT_EXISTS);
        }
        if (parentDataId == DataEnum.ZERO_FOLDER.getIndex()) {
            throw new AppException(AppExceptionCodeMsg.FOLDER_NOT_EXISTS);
        }
        List<DataDetInfoDto> dataList = dataShareService.infoShareData(parentDataId,passCode,shareId);
        return ResponseResult.success(dataList);
    }


    @ApiOperation(value = "无权限获取路径")
    @GetMapping("/getDataPath/{dataId}")
    public ResponseResult getDataPath(@PathVariable Integer dataId,Integer shareId,String passCode) {
        if (Objects.isNull(dataId) || Objects.isNull(shareId)) {
            throw new AppException(AppExceptionCodeMsg.DATA_NOT_EXISTS);
        }
        List<DataPathDto> dataPath = dataService.getDataPath(dataId,shareId,passCode);
        return ResponseResult.success(dataPath);
    }


    @ApiOperation(value = "无权限获取路径和遍历文件")
    @GetMapping("/getDataPathAndData/{dataId}")
    public ResponseResult getDataPathAndData(@PathVariable Integer dataId,Integer shareId,String passCode) {
        if (Objects.isNull(dataId) || Objects.isNull(shareId)) {
            throw new AppException(AppExceptionCodeMsg.DATA_NOT_EXISTS);
        }
        List<Object> dataPath = dataService.getDataPathAndData(dataId,shareId,passCode);
        return ResponseResult.success(dataPath);
    }

}