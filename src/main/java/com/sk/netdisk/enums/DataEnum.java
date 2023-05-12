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

    FOLDER(0,"文件夹类型"),
    MAX_NONE_FOLDER(0,"最顶级虚无文件夹"),
    ACCESS_TO_FINAL_DEL(1,"有权限删除回收站文件")
    ;

    private final int index;

    private final String desc;
}
