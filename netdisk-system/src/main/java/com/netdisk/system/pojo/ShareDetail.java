package com.netdisk.system.pojo;

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
 * @TableName share_detail
 */
@TableName(value ="share_detail")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareDetail implements Serializable {
    /**
     * 
     */
    @TableId
    private Integer id;

    /**
     * 
     */
    private Integer lookNum;

    /**
     * 
     */
    private Integer saveNum;

    /**
     * 
     */
    private Integer downloadNum;

    /**
     * 
     */
    private Integer dataShareId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}