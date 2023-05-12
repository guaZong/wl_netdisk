package com.sk.netdisk.config.security.phoneSecurityConfig;

import com.sk.netdisk.config.security.TokenManager;
import com.sk.netdisk.util.Redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Administrator
 */
@Component
@RequiredArgsConstructor
public class PhoneCodeSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Qualifier("PhoneDetailServiceImpl")
    @Autowired
    private UserDetailsService phoneDetailsService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private TokenManager tokenManager;

    @Override
    public void configure(HttpSecurity http) {

        PhoneAuthenticationFilter p=new PhoneAuthenticationFilter("/sysVisitor/clogin",redisUtil,tokenManager);
        p.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        p.setAuthenticationSuccessHandler(new PhoneSuccessHandler(tokenManager,redisUtil));
        p.setAuthenticationFailureHandler(new PhoneFailHandler(tokenManager));
        PhoneCodeAuthenticationProvider pc=new PhoneCodeAuthenticationProvider(redisUtil);
        pc.setUserDetailsService(phoneDetailsService);
        http.authenticationProvider(pc).addFilterAfter(p, UsernamePasswordAuthenticationFilter.class);

    }

}
