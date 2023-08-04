package com.netdisk.common.annotation;

import com.netdisk.common.enums.FunctionTypeEnum;
import com.netdisk.common.enums.OperateDeviceTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lsj
 * @date 2023/8/4 11:12
 * @description 日志注解
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    /**
     * 接口模块
     * @return String
     */
    public String apiModule() default "";

    /**
     * 方法类型
     * @return String
     */
    public FunctionTypeEnum functionType() default FunctionTypeEnum.OTHER;

    /**
     * 操作设备类型
     */
    public OperateDeviceTypeEnum operatorType() default OperateDeviceTypeEnum.PC_COMMON;

    /**
     * 是否保存请求的参数
     */
    public boolean isSaveRequestData() default true;

    /**
     * 是否保存响应的参数
     */
    public boolean isSaveResponseData() default true;
}
