package com.netdisk.system.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lsj
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer userId;

    /**
     * 账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别(0男,1女)

     */
    private Integer sex;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 登录地址

     */
    private String loginIp;

    /**
     * 登录日期
     */
    private Date loginDate;

    /**
     * 身份状态
     */
    private Integer status;

    /**
     * 是否是vip
     */
    private Integer isVip;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 是否删除,0不删除,1删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 是否锁定
     */
    private Integer isLock;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}