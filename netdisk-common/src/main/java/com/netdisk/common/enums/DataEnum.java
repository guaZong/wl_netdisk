package com.netdisk.common.enums;

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
    OVERRIDE_COPY(1,"复制覆盖操作"),
    OVERRIDE_SHEAR(2,"剪切覆盖操作"),
    OVERRIDE_RESTORE(3,"还原覆盖操作"),
    OVERRIDE_SAVE_SOURCE(4,"保存分享资源覆盖操作"),
    GENERATE_COPY(1,"复制追加操作"),
    GENERATE_SHEAR(2,"剪切追加操作"),
    GENERATE_RESTORE(3,"还原追加操作"),
    GENERATE_SAVE_SOURCE(4,"保存分享资源追加操作"),
    SORT_TYPE_NAME(1,"根据文件名排序"),
    SORT_TYPE_TIME(2,"根据最后修改时间排序"),
    SORT_TYPE_SIZE(3,"根据文件大小排序,不包括文件夹"),
    SORT_ORDER_DESC(1,"降序排序"),
    SORT_ORDER_ESC(2,"升序排序"),
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
