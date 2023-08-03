package com.netdisk.framework.security.handler;




import com.netdisk.common.enums.AppExceptionCodeMsg;
import com.netdisk.common.core.ResponseResult;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @author lsj
 * @description 无权限处理器
 */
public class UnauthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
      ResponseResult.out(response,ResponseResult.error(AppExceptionCodeMsg.USER_NOT_LOGIN));
    }
}
