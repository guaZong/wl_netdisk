package com.netdisk.common.exception;


import com.netdisk.common.enums.AppExceptionCodeMsg;

/**
 * 自定义异常类
 * @author lsj
 */
public class AppException extends RuntimeException{

    private final int code;
    private final String message;

    public AppException(AppExceptionCodeMsg appExceptionCodeMsg){
        super();
        this.code=appExceptionCodeMsg.getCode();
        this.message=appExceptionCodeMsg.getMessage();
    }

    public AppException(int code,String message){
        super();
        this.code=code;
        this.message=message;
    }

    public int getCode(){
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
