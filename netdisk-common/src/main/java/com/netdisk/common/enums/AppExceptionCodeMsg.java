package com.netdisk.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务异常枚举类
 * @author lsj
 */
@Getter
@AllArgsConstructor
public enum AppExceptionCodeMsg {
    //通用异常
    BUSY(10000,"服务器繁忙"),
    INVALID_PERMISSION(10001,"无权限访问"),
    NAME_IS_NULL(10002,"名字为空"),
    UPLOAD_FILE_IS_NULL(10003,"上传的文件为空"),
    INVALID_CODE(10004,"手机验证码错误"),
    NULL_VALUE(10005,"请将数据填写完整"),
    SERVER_EXCEPTION(10006,"系统异常"),
    SERVER_ERR(10007,"系统错误"),


    //用户类相关异常
    USER_NOT_LOGIN(11001,"用户未登录"),
    PHONE_IS_NULL(11002,"电话号码为空"),
    PHONE_FORMAT_INVALID(11003,"电话号码无效"),
    EMAIL_FORMAT_INVALID(11004,"邮箱无效"),
    EMAIL_REBIND_ERR(11005,"该邮箱以被绑定"),
    PHONE_REBIND_ERR(11006,"该电话以被绑定"),
    RE_PASSWORD_INVALID(11007,"两次密码输入不一致"),

    //文件类相关异常
    DATA_NOT_ENTER(12001,"不能进入文件"),
    DATA_NOT_EXISTS(12002,"文件不存在"),
    FOLDER_NOT_EXISTS(12004,"父级文件夹不存在"),
    DATA_NAME_TOO_LONG(12005,"文件名字过长"),
    DATA_COPY_ERR(12006,"不能将文件复制到自身或其子目录下"),
    DATA_SHEAR_ERR(12007,"不能将文件移动到自身或其子目录下"),
    DATA_NUM_TOO_LARGE(12008,"文件数目过多"),
    DATA_RENAME(12009,"文件重名"),
    PASSCODE_INVALID(12010,"提取码错误"),
    SHARE_INVALID(12011,"分享已取消"),
    SHARE_IS_DELETE(12012,"文件已被删除"),
    PASSCODE_LENGTH_INVALID(12013,"提取码格式错误"),
    ;


    private final Integer code;

    private final String message;

}
