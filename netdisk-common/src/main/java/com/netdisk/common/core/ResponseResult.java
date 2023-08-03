package com.netdisk.common.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netdisk.common.enums.AppExceptionCodeMsg;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author lsj
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseResult<T> implements Serializable {

    /**
     * 服务端的返回码
     */
    private Integer code;

    /**
     * 服务端的返回信息
     */
    private String message;

    /**
     * 服务端返回的数据
     */
    private T data;

    /**
     * 返回成功信息与数据
     *
     * @param message 成功的信息
     * @param object  数据
     * @return ResponseResult
     */
    public static ResponseResult success(String message, Object... object) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.message = message;
        responseResult.data = object;
        responseResult.code = 200;
        return responseResult;
    }

    /**
     * 返回成功的单个数据
     *
     * @param object 数据
     * @return ResponseResult
     */
    public static ResponseResult success(Object object) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.message = "操作成功";
        responseResult.data = object;
        responseResult.code = 200;
        return responseResult;
    }

    /**
     * 返回成功的多个数据
     *
     * @return ResponseResult
     */
    public static ResponseResult success() {
        ResponseResult responseResult = new ResponseResult();
        responseResult.message = "操作成功";
        responseResult.code = 200;
        return responseResult;
    }

    /**
     * 返回错误码与信息
     *
     * @param code    错误码
     * @param message 错误信息
     * @return ResponseResult
     */
    public static ResponseResult error(Integer code, String message) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.message = message;
        responseResult.code = code;
        return responseResult;
    }

    /**
     * 返回失败的信息
     *
     * @param msg 失败信息
     * @return ResponseResult
     */
    public static ResponseResult error(String msg) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.code = 500;
        responseResult.message = msg;
        return responseResult;
    }

    /**
     * 返回自定义的失败信息
     *
     * @param appExceptionCodeMsg 自定义类
     * @return ResponseResult
     */
    public static ResponseResult error(AppExceptionCodeMsg appExceptionCodeMsg) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.message = appExceptionCodeMsg.getMessage();
        responseResult.code = appExceptionCodeMsg.getCode();
        return responseResult;
    }

    /**
     * 返回自定义的失败信息
     *
     * @param appExceptionCodeMsg 自定义类
     * @return ResponseResult
     */
    public static ResponseResult error(AppExceptionCodeMsg appExceptionCodeMsg, Object... data) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.message = appExceptionCodeMsg.getMessage();
        responseResult.code = appExceptionCodeMsg.getCode();
        responseResult.data = data;
        return responseResult;
    }

    public static void out(HttpServletResponse response, ResponseResult r) {
        ObjectMapper mapper = new ObjectMapper();
        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            mapper.writeValue(response.getWriter(), r);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

