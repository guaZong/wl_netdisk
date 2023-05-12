package com.sk.netdisk.config.security.Filter;



import com.sk.netdisk.config.security.TokenManager;
import com.sk.netdisk.pojo.security.SecurityUser;
import com.sk.netdisk.util.ResponseResult;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Administrator
 */
public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {

    private TokenManager tokenManager;
    private AuthenticationManager authenticationManager;

    public TokenLoginFilter(TokenManager tokenManager, AuthenticationManager authenticationManager) {
        this.tokenManager = tokenManager;
        this.authenticationManager = authenticationManager;
        this.setPostOnly(false);
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/sysVisitor/plogin"));
    }

    /**
     * 获取表单提交的内容
     *
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        SecurityUser securityUser = (SecurityUser) authResult.getPrincipal();
        Map<String, String> map = new HashMap<>();
        map.put("userId",securityUser.getUserId().toString());
        map.put("role",securityUser.getPermissionValueList().get(0));
        String token = tokenManager.createToken(map);
        String refreshToken = tokenManager.createToken(map);
        HashMap<String, String> result = new HashMap<>();
        result.put("token", token);
        result.put("refresh_token",refreshToken);
        logger.info(securityUser.getUsername() + ":登录成功   时间：" + LocalDateTime.now());
        ResponseResult.out(response, ResponseResult.success(result));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        ResponseResult.out(response, ResponseResult.error("账号或密码有误！"));
    }
}
