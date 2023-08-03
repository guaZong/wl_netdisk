package com.netdisk.system.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * 文件详细信息
 *
 * @author lsj
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataInfoVo {
    /**
     * 文件类型
     */
    private String type;
    /**
     * 文件位置
     */
    private String position;
    /**
     * 文件大小
     */
    private String dataSize;
    /**
     * 包含文件数量
     */
    private Integer folderNum;
    /**
     * 文件数量
     */
    private Integer fileNum;
    /**
     * 修改时间
     */
    private String updateTime;
}
