package com.sk.netdisk.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName transfer
 */
@TableName(value ="transfer")
@Data
public class Transfer implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 文件id
     */
    private Integer dataId;

    /**
     * 文件名字
     */
    private String fileName;

    /**
     * 传输状态,0代表上传中,1代表下载中,2代表上传完成,3代表下载完成
     */
    private Integer status;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 
     */
    private Integer createBy;

    /**
     * 
     */
    private Integer updateBy;

    /**
     * 
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}