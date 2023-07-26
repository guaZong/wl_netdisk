package com.sk.netdisk.controller;

import cn.hutool.core.util.ObjectUtil;
import com.sk.netdisk.controller.request.GeneralRequest;
import com.sk.netdisk.enums.AppExceptionCodeMsg;
import com.sk.netdisk.exception.AppException;
import com.sk.netdisk.pojo.dto.UserInfoDto;
import com.sk.netdisk.service.UserService;
import com.sk.netdisk.util.ResponseResult;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



/**
 * 用户API
 * @author Administrator
 */
@RequestMapping("/sysUser")
@RestController
@CrossOrigin
public class UserController {

    @Autowired
    UserService userService;

    @ApiOperation(value = "用户设置密码")
    @PostMapping("/makePwd")
    public ResponseResult makePwd(@RequestBody GeneralRequest generalRequest) {
        String newPassword=generalRequest.getPassword();
        String repeatPassword=generalRequest.getRePassword();
        if(StringUtils.isAnyBlank(newPassword,repeatPassword)){
            throw new AppException(AppExceptionCodeMsg.NULL_VALUE);
        }
        if(!newPassword.equals(repeatPassword)){
            throw new AppException(AppExceptionCodeMsg.RE_PASSWORD_INVALID);
        }
        userService.makePwd(newPassword,repeatPassword);
        return ResponseResult.success();
    }

    @ApiOperation(value = "绑定邮箱")
    @PostMapping("/bindEmail")
    public ResponseResult bindEmail(@RequestBody GeneralRequest generalRequest) {
        String email = generalRequest.getEmail();
        String code = generalRequest.getCode();
        if(StringUtils.isAnyBlank(code,email)){
            throw new AppException(AppExceptionCodeMsg.NULL_VALUE);
        }
        UserInfoDto userInfoDto = userService.bindEmail(code, email);
        return ResponseResult.success(userInfoDto);
    }


    @ApiOperation(value = "发送绑定邮箱的验证码")
    @GetMapping("/bindEmail/sendECode")
    public ResponseResult sendBECode(String email) {
        if(StringUtils.isAnyBlank(email)){
            throw new AppException(AppExceptionCodeMsg.NULL_VALUE);
        }
        String code = userService.sendBingEmailCode(email);
        return ResponseResult.success(code);
    }



    @ApiOperation(value = "修改头像")
    @PutMapping("/updateAvatar")
    public ResponseResult updateAvatar(MultipartFile avatar) {
        if(ObjectUtil.isEmpty(avatar)){
            throw new AppException(AppExceptionCodeMsg.NULL_VALUE);
        }
        UserInfoDto userInfoDto = userService.updateAvatar(avatar);
        return ResponseResult.success(userInfoDto);
    }


    @ApiOperation(value = "修改昵称")
    @PutMapping("/updateNickname")

    public ResponseResult updateNickname(String  nickname) {
        if(ObjectUtil.isEmpty(nickname)){
            throw new AppException(AppExceptionCodeMsg.NULL_VALUE);
        }
        UserInfoDto userInfoDto = userService.updateNickname(nickname);
        return ResponseResult.success(userInfoDto);
    }

    @ApiOperation(value = "获取某用户存储空间")
    @GetMapping("/getStorage")
    public ResponseResult getStorage() {
        String getStorage = userService.getStorage();
        return ResponseResult.success(getStorage);
    }

    @ApiOperation(value = "获取登录后信息")
    @GetMapping("/infoUser")
    public ResponseResult infoUser() {
        UserInfoDto infoUser = userService.infoUser();
        return ResponseResult.success(infoUser);
    }
}
