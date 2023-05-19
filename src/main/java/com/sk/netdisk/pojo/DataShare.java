package com.sk.netdisk.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @TableName data_share
 */
@TableName(value = "data_share")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
     * 允许查看人数状态(0是有人数限制,1是无人数限制,2是过期,3是被人删除)
     */
    private Integer accessStatus;

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

    /**
     * 文件id
     */
    @TableField(exist = false)
    private Set<Integer> dataIds;


    private Integer expireDays;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    public DataShare(String link, String passCode, Integer accessNum, Integer accessStatus,
                     Date createTime, Integer createBy, Integer expireDays) {
        this.link = link;
        this.passCode = passCode;
        this.accessNum = accessNum;
        this.accessStatus = accessStatus;
        this.createTime = createTime;
        this.createBy = createBy;
        this.expireDays = expireDays;
    }

    public DataShare(String link, String passCode, Integer accessStatus,
                     Date createTime, Integer createBy, Integer expireDays) {
        this.link = link;
        this.passCode = passCode;
        this.accessStatus = accessStatus;
        this.createTime = createTime;
        this.createBy = createBy;
        this.expireDays = expireDays;
    }
}