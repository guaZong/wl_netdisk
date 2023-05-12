package com.sk.netdisk.config.security.phoneSecurityConfig;


import com.sk.netdisk.config.security.TokenManager;
import com.sk.netdisk.enums.AppExceptionCodeMsg;
import com.sk.netdisk.exception.AppException;
import com.sk.netdisk.util.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class PhoneFailHandler implements AuthenticationFailureHandler {
    TokenManager tokenManager;
    public PhoneFailHandler(TokenManager tokenManager){
        this.tokenManager=tokenManager;
    }
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        throw new AppException(AppExceptionCodeMsg.INVALID_CODE);

    }
}
