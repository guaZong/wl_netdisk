package com.sk.netdisk.config.security.phoneSecurityConfig;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sk.netdisk.config.security.TokenManager;
import com.sk.netdisk.enums.AppExceptionCodeMsg;
import com.sk.netdisk.exception.AppException;
import com.sk.netdisk.util.Redis.RedisUtil;
import com.sk.netdisk.util.ResponseResult;
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
import java.io.InputStream;

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
        String username = "";
        String code = "";
        try {
            InputStream inputStream = request.getInputStream();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(inputStream);
            username = jsonNode.get("username").asText();
            code = jsonNode.get("code").asText();
        } catch (IOException e) {
            ResponseResult.out(response,ResponseResult.error(AppExceptionCodeMsg.BUSY));
        }
        PhoneAuthenticationToken authRequest = new PhoneAuthenticationToken(username, code);
        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected void setDetails(HttpServletRequest request, PhoneAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }



}
