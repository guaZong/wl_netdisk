package com.sk.netdisk.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName file
 */
@TableName(value ="file")
@Data
public class File implements Serializable {
    /**
     * FileId
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 文件MD5
     */
    private String md5;

    /**
     * 文件链接
     */
    private String link;

    /**
     * 真实文件创建时间
     */
    private Date createTime;

    /**
     * 创建者
     */
    private Integer createBy;
    /**
     * 文件大小-->格式化之后的
     */
    private String size;
    /**
     * 文件字节大小
     */
    private String bytes;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}