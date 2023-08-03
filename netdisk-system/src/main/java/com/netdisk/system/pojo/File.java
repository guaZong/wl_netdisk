package com.netdisk.system.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lsj
 * @TableName file
 */
@TableName(value ="file")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    @JsonFormat(pattern = "yyyy年MM月dd日,HH:mm")
    private Date createTime;

    /**
     * 创建者
     */
    private Integer createBy;

    public File(String md5, String link, Integer createBy, Date createTime,String size, String bytes) {
        this.md5 = md5;
        this.link = link;
        this.createBy = createBy;
        this.createTime=createTime;
        this.size = size;
        this.bytes = bytes;
    }

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