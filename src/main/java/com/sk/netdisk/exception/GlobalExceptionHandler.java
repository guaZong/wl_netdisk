package com.sk.netdisk.exception;

import com.sk.netdisk.util.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 * @author lsj
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    public ResponseResult exceptionHandler(Exception e){
        //判断拦截到的Exception是自己自定义的异常类型
        if(e instanceof AppException){
            AppException appException= (AppException) e;
            return ResponseResult.error(appException.getCode(),appException.getMessage());
        }
        //这个拦截到的异常不是自定义的异常-->数组越界,数学异常,数据库异常
        log.error(e.getMessage());
        return ResponseResult.error("服务器繁忙,请稍后再试");
    }


}
