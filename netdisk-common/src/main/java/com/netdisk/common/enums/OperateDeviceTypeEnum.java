package com.netdisk.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lsj
 * @description OperateDeviceTypeEnum
 * @createDate 2023/8/4 14:04
 */
@Getter
@AllArgsConstructor
public enum OperateDeviceTypeEnum {

    /**
     * PC端普通用户
     */
    PC_COMMON(1),
    /**
     * PC端管理员
     */
    PC_ADMIN(2),
    /**
     * 手机端
     */
    PHONE(3);

    private final Integer type;
}
