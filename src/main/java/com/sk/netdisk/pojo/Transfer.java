package com.sk.netdisk.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
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
     * 文件md5
     */
    private String md5;

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
    @JsonFormat(pattern = "yyyy年MM月dd日,HH:mm")
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

    public Transfer(String md5, String fileName, Integer status, Date createTime, Integer createBy) {
        this.md5 = md5;
        this.fileName = fileName;
        this.status = status;
        this.createTime = createTime;
        this.createBy = createBy;
    }

    /**
     * 
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}