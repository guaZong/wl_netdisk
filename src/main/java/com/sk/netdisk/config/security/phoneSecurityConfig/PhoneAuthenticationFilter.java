package com.sk.netdisk.config.security.phoneSecurityConfig;


import com.sk.netdisk.config.security.TokenManager;
import com.sk.netdisk.util.Redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Administrator
 */
@Slf4j
public class PhoneAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String FORM_PHONE_KEY = "username";
    public static final String FORM_PHONE_CODE_KEY = "code";

    private RedisUtil redisUtil;
    private TokenManager tokenManager;

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER
            = new AntPathRequestMatcher("/sysVisitor/phoneLogin", "POST");

    private boolean postOnly = true;

    public PhoneAuthenticationFilter(String defaultFilterProcessesUrl, RedisUtil redisUtil,TokenManager tokenManager) {
        super(defaultFilterProcessesUrl);
        this.redisUtil=redisUtil;
        this.tokenManager=tokenManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        String mobile = obtainPhone(request);
        mobile = (mobile != null) ? mobile : "";
        mobile = mobile.trim();
        String smsCode = obtainPhoneCode(request);
        smsCode = (smsCode != null) ? smsCode : "";
        PhoneAuthenticationToken authRequest = new PhoneAuthenticationToken(mobile, smsCode);
        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private String obtainPhone(HttpServletRequest request) {
        return request.getParameter(FORM_PHONE_KEY);
    }

    private String obtainPhoneCode(HttpServletRequest request) {
        return request.getParameter(FORM_PHONE_CODE_KEY);
    }

    protected void setDetails(HttpServletRequest request, PhoneAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }



}
