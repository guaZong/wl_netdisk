package com.sk.netdisk.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName data_share
 */
@TableName(value ="data_share")
@Data
public class DataShare implements Serializable {
    /**
     * 分享表id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 链接地址
     */
    private String link;

    /**
     * 分享码
     */
    private String passCode;

    /**
     * 允许查看人数
     */
    private Integer accessNum;

    /**
     * 允许查看人数状态(0是有人数限制,1是无人数限制)
     */
    private Integer accessStatus;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 创建人(上传文件的人)
     */
    private Integer createBy;

    /**
     * 
     */
    private Integer updateBy;

    /**
     * 是否删除,0不删除,1删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}