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
 * @TableName quick_data
 */
@TableName(value ="quick_data")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuickData implements Serializable {
    /**
     * 快速访问id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 文件id
     */
    private Integer dataId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public QuickData(Integer userId, Integer dataId) {
        this.userId = userId;
        this.dataId = dataId;
    }
}