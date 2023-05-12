package com.sk.netdisk.constant;

import com.sk.netdisk.util.UserUtil;

/**
 * 存放Redis的一些key值
 *
 * @author lsj
 */
public class RedisConstants {

    /**
     * 登录验证码 redis key
     */
    public static final String PHONE_LOGIN_KEY = "phone_login:";

    /**
     * 删除回收站 redis key
     */
    public static final String PHONE_FINAL_DEL_KEY="phone_finalDel:";

    /**
     * 基础缓存  redis key
     */
    public static final String CACHE_CODE_KEY = "cache_code:";

    /**
     * 找回密码的手机验证码缓存 redis key
     */
    public static final String FIND_PWD_P_CODE = CACHE_CODE_KEY + "findPwd:";

    /**
     * 绑定邮箱的的邮箱验证码缓存 redis key
     */
    public static final String BIND_EMAIL_E_CODE = CACHE_CODE_KEY + "bindEmail:";

    /**
     * 回收站访问删除 redis key
     */
    public static final String FINAL_DEL_KEY = CACHE_CODE_KEY+"delFinal:";
    /**
     * 真实文件 redis key
     */
    public static final String FILE_KEY = "netdisk:"+"file:";



}
