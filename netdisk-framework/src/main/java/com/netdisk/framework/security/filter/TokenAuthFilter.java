package com.netdisk.framework.security.filter;

import com.netdisk.common.core.domain.SecurityUser;
import com.netdisk.framework.web.TokenManager;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * @author lsj
 * @description 鉴权,使用方法的时候获取token进行读取userId鉴权
 */
public class TokenAuthFilter extends BasicAuthenticationFilter {

    private TokenManager tokenManager;



    public TokenAuthFilter(AuthenticationManager authenticationManager, TokenManager tokenManager) {
        super(authenticationManager);
        this.tokenManager = tokenManager;
    }

    /**
     * 传入的Throwable强转为RuntimeException
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
        chain.doFilter(request, response);
    }

    /**
     * 获取权限信息
     *
     * @param request
     * @return
     */
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (token != null && tokenManager.verfyToken(token)) {
            Map<String, String> map = tokenManager.getUserInfoFromToken(token);
            if (map == null) {
                return null;
            }
            String userId = map.get("userId");
            String role = map.get("role");
            List<String> list = new ArrayList<>();
            SecurityUser securityUser = new SecurityUser(Integer.parseInt(userId),list);
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            return new UsernamePasswordAuthenticationToken(securityUser, token, authorities);
        }
        return null;
    }
}
