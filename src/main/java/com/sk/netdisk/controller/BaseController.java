package com.sk.netdisk.controller;

import com.sk.netdisk.controller.request.GeneralRequest;
import com.sk.netdisk.enums.AppExceptionCodeMsg;
import com.sk.netdisk.exception.AppException;
import com.sk.netdisk.service.DataService;
import com.sk.netdisk.service.UserService;
import com.sk.netdisk.service.impl.DataServiceImpl;
import com.sk.netdisk.util.Redis.RedisIdWorker;
import com.sk.netdisk.util.Redis.RedisUtil;
import com.sk.netdisk.util.Regex.RegexUtils;
import com.sk.netdisk.util.ResponseResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 无需认证接口
 * @author Administrator
 */
@RestController
//@CrossOrigin
@RequestMapping("/sysVisitor")
public class BaseController {

    UserService userService;

    RedisUtil redisUtil;

    RedisIdWorker redisIdWorker;

    DataService dataService;
    @Autowired
    DataServiceImpl dataService1;

    public BaseController(UserService userService, RedisUtil redisUtil,
                          RedisIdWorker redisIdWorker, DataService dataService) {
        this.userService = userService;
        this.redisUtil = redisUtil;
        this.redisIdWorker = redisIdWorker;
        this.dataService = dataService;
    }


    @ApiOperation(value = "根据手机号发送登录注册验证码")
    @GetMapping("/sendCode")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "username", value = "手机号码",required = true)
            }
    )
    public ResponseResult sendCode(String username) {
        if(StringUtils.isAnyBlank(username)){
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
        String password = generalRequest.getPassword();
        String rePassword = generalRequest.getRePassword();
        String code = generalRequest.getCode();
        if(StringUtils.isAnyBlank(password,rePassword,phoneNumber,code)){
            throw new AppException(AppExceptionCodeMsg.NULL_VALUE);
        }
        if(!RegexUtils.isPhoneInvalid(phoneNumber)){
            throw new AppException(AppExceptionCodeMsg.PHONE_FORMAT_INVALID);
        }
        if(!password.equals(rePassword)){
            throw new AppException(AppExceptionCodeMsg.RE_PASSWORD_INVALID);
        }
        return userService.pSetNewPwd(phoneNumber,code,password,rePassword);
    }


    @ApiOperation(value = "发送删除回收站的验证码")
    @GetMapping("/sendFinalDelCode")
    public ResponseResult sendFinalDelCode(String phoneNumber) {
        if(StringUtils.isAnyBlank(phoneNumber)){
            throw new AppException(AppExceptionCodeMsg.NULL_VALUE);
        }
        if(!RegexUtils.isPhoneInvalid(phoneNumber)){
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



}
