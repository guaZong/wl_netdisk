package com.sk.netdisk.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件类
 * @author lsj
 * @TableName data
 */
@TableName(value ="data")
@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
public class Data implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    public Data(Integer id, Integer isDelete) {
        this.id = id;
        this.isDelete = isDelete;
    }

    /**
     * 名称
     */
    private String name;


    /**
     *  0代表文件夹
        1代表图片
        2代表视频
        3代表文档
        4代表音乐
        5代表种子
        6代表压缩包
        7代表其他
     */
    private Integer type;

    /**
     * 父级目录id
     */
    private Integer parentDataId;

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


    private Integer fileId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public Data(String name, Integer type, Integer parentDataId,
                Date createTime,Date updateTime, Integer createBy, Integer fileId) {
        this.name = name;
        this.type = type;
        this.parentDataId = parentDataId;
        this.createTime = createTime;
        this.updateTime=updateTime;
        this.createBy = createBy;
        this.fileId = fileId;
    }

    public Data(String name, Integer type, Integer parentDataId,
                Date createTime,Date updateTime, Integer createBy) {
        this.name = name;
        this.type = type;
        this.parentDataId = parentDataId;
        this.createTime = createTime;
        this.updateTime=updateTime;
        this.createBy = createBy;
    }
}