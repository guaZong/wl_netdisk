package com.netdisk.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lsj
 * 传输枚举类
 */
@Getter
@AllArgsConstructor
public enum TransferEnum {
    /**
     * 传输枚举标识
     */
    UPLOAD_TRANSFER(0,"上传中"),
    DOWNLOAD_TRANSFER(1,"下载中"),
    UPLOAD_COMPLETE(2,"上传完毕"),
    DOWNLOAD_COMPLETE(3,"下载完毕"),
    UPLOAD_CANCEL(4,"上传取消"),
    DOWNLOAD_CANCEL(5,"下载取消"),
    UPLOAD_FAIL(6,"上传失败"),
    DOWNLOAD_FAIL(7,"下载失败"),
    ;

    /**
     * 枚举标识
     */
    private final int index;

    /**
     * 文档描述
     */
    private final String desc;
}
