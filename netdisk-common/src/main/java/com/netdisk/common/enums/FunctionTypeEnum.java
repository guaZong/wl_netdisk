package com.netdisk.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lsj
 * @description FunctionTypeEnum
 * @createDate 2023/8/4 11:21
 */
@Getter
@AllArgsConstructor
public enum FunctionTypeEnum {
    /**
     * type
     */
    OTHER("其他"),
    INSERT("新增"),
    UPDATE("修改"),
    DELETE("删除");



    private final String type;
}
