package com.netdisk.system.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 计算文件详细信息
 * @author lsj
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecurCountSizeInfo {

    private Integer folderNum;
    private Integer fileNum;
    private long dataSize;

    public RecurCountSizeInfo(long dataSize) {
        this.dataSize = dataSize;
    }
}
