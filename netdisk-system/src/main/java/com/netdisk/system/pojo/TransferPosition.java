package com.netdisk.system.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * @author lsj
 * @TableName transfer_position
 */
@TableName(value ="transfer_position")
@Data
public class TransferPosition implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private String position;

    /**
     * 0代表上传完成,1代表下载完成
     */
    private Integer status;

    /**
     * 
     */
    private Integer transferId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}