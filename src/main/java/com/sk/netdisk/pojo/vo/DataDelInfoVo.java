package com.sk.netdisk.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 回收站实体类
 * @author lsj
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataDelInfoVo {
    /**
     * 回收站文件id
     */
    private Integer id;

    /**
     * 文件id
     */
    private Integer dataId;

    /**
     * 文件名称
     */
    private String name;

    /**
     * 文件类型
     */
    private Integer type;

    /**
     * 创建人
     */
    private Integer createBy;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 距离删除天数
     */
    private Integer days;
}
