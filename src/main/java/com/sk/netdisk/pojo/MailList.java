package com.sk.netdisk.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName mail_list
 */
@TableName(value ="mail_list")
@Data
public class MailList implements Serializable {
    /**
     * 通讯录id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 好友id,如果status状态是0的话,这个必须有值,反之无值
     */
    private Integer freId;

    /**
     * 群id
     */
    private Integer groupId;

    /**
     * 0是好友,1是添加群组,2是群主
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 是否删除,0不删除,1删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}