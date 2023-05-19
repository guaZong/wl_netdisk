package com.sk.netdisk.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 
 * @TableName data_del
 */
@TableName(value ="data_del")
@Data
public class DataDel implements Serializable {
    /**
     * 回收id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 文件id
     */
    private Integer dataId;

    /**
     * 创建人
     */
    private Integer createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy年MM月dd日,HH:mm")
    private Date createTime;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}