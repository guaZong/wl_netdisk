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
 * @author lsj
 * @description 用户提交表单进行拦截,并将请求信息封装未Authentication
 */
@Slf4j
public class PhoneAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER
            = new AntPathRequestMatcher("/sysVisitor/phoneLogin", "POST");

    public PhoneAuthenticationFilter(String defaultFilterProcessesUrl, RedisUtil redisUtil,TokenManager tokenManager) {
        super(defaultFilterProcessesUrl);
    }
    public final String POST="POST";
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        if (!POST.equals(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        //读取json 获取手机号和验证码
        InputStream inputStream = request.getInputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(inputStream);
        String username = jsonNode.get("username").asText();
        String code = jsonNode.get("code").asText();
        //生成PhoneAuthenticationToken->未认证的token对象
        PhoneAuthenticationToken authRequest = new PhoneAuthenticationToken(username, code);
        // details保存与认证过程有关的任意信息
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected void setDetails(HttpServletRequest request, PhoneAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }



}
