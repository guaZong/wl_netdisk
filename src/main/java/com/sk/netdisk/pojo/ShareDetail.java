package com.sk.netdisk.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * @author lsj
 * @TableName share_detail
 */
@TableName(value ="share_detail")
@Data
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
    private Integer dataHareId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}