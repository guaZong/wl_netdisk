package com.sk.netdisk.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Data 枚举类
 * @author lsj
 */
@Getter
@AllArgsConstructor
public enum DataEnum {
    /**
     * 文件夹类型
     */
    FOLDER(0,"文件夹类型"),
    ZERO_FOLDER(0,"最顶级文件夹"),
    ACCESS_TO_FINAL_DEL(1,"有权限删除回收站文件"),
    SHARE_IS_LIMIT(0,"文件分享限制人数"),
    SHARE_NO_LIMIT(1,"文件分享不限制人数"),
    SHARE_IS_EXPIRE(2,"文件分享已过期"),
    SHARE_IS_DELETE(3,"文件已被删除"),
    SHARE_IS_BOUNDLESS(-1,"文件没有过期时间"),
    ;


    /**
     * 获取id
     */
    private final int index;
    /**
     * 文档描述
     */
    private final String desc;
}
