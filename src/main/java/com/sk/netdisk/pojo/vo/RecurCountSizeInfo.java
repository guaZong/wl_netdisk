package com.sk.netdisk.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Administrator
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
