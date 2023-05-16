package com.sk.netdisk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sk.netdisk.pojo.User;
import com.sk.netdisk.pojo.dto.UserInfoDto;
import com.sk.netdisk.util.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
* @author lsj
* @description 针对表【user】的数据库操作Service
* @createDate 2022-11-29 10:50:12
*/
public interface UserService extends IService<User> {

    /**
     * 发送手机验证码
     * @param username 手机号
     * @return String
     */
    String sendPhoneCode(String username);

    /**
     * 登陆后设置密码
     * @param newPassword 新的密码
     * @param repeatPassword 重复密码
     */
    void makePwd(String newPassword, String repeatPassword);

    /**
     * 发送找回密码的手机验证码
     * @param phoneNumber 手机号
     * @return String
     */
    String sendFindPwdPhoneCode(String phoneNumber);

    /**
     * 通过手机验证码找回密码
     * @param code 验证码
     * @param newPassword 新密码
     * @param repeatPassword 重复密码
     * @param phoneNumber 手机号
     * @return ResponseResult
     */
    ResponseResult pSetNewPwd(String phoneNumber,String code,String newPassword, String repeatPassword);

    /**
     * 绑定邮箱
     * @param code 邮箱验证码
     * @param email 邮箱
     * @return UserInfoDto
     */
    UserInfoDto bindEmail(String code, String email);

    /**
     * 发送绑定邮箱的验证码
     * @param email 邮箱
     * @return String
     */
    String sendBingEmailCode(String email);

    /**
     * 修改头像
     * @param avatar 头像文件
     * @return UserInfoDto
     */
    UserInfoDto updateAvatar(MultipartFile avatar);

    /**
     * 修改昵称
     * @param nickname 昵称
     * @return UserInfoDto
     */
    UserInfoDto updateNickname(String nickname);

    /**
     * 发送删除回收站验证码
     * @param phoneNumber 手机号
     * @return String
     */
    String senFinalDelCode(String phoneNumber);

    /**
     * 获取用户使用的存储空间
     * @return 存储空间
     */
    String getStorage();

    /**
     * 遍历自己登录后的用户信息
     * @return UserInfoDto
     */
    UserInfoDto infoUser();
}
