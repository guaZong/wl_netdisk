package com.netdisk.framework.security.handler;


import com.netdisk.common.enums.AppExceptionCodeMsg;
import com.netdisk.common.core.ResponseResult;
import com.netdisk.framework.web.TokenManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lsj
 * @description 手机登录失败处理器
 */
@Slf4j
public class PhoneFailHandler implements AuthenticationFailureHandler {
    TokenManager tokenManager;
    public PhoneFailHandler(TokenManager tokenManager){
        this.tokenManager=tokenManager;
    }
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        ResponseResult.out(response,ResponseResult.error(AppExceptionCodeMsg.INVALID_CODE));
    }
}
