package com.sk.netdisk.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName data_list
 */
@TableName(value ="data_list")
@Data
public class DataList implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer dataId;

    /**
     * 
     */
    private Integer groupId;

    /**
     * 
     */
    @JsonFormat(pattern = "yyyy年MM月dd日,HH:mm")
    private Date createTime;

    /**
     * 
     */
    @JsonFormat(pattern = "yyyy年MM月dd日,HH:mm")
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