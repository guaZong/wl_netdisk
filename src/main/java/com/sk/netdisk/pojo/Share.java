package com.sk.netdisk.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lsj
 * @TableName share
 */
@TableName(value ="share")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Share implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer shareId;

    /**
     * 
     */
    private Integer dataId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public Share(Integer shareId, Integer dataId) {
        this.shareId = shareId;
        this.dataId = dataId;
    }
}