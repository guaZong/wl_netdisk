package com.sk.netdisk.config.security;

import com.sk.netdisk.config.security.Filter.TokenAuthFilter;
import com.sk.netdisk.config.security.Filter.TokenLoginFilter;
import com.sk.netdisk.config.security.handler.SimpleAccessDeniedHandler;
import com.sk.netdisk.config.security.handler.UnauthEntryPoint;
import com.sk.netdisk.config.security.phoneSecurityConfig.PhoneCodeSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * springsecurity  配置类
 * @author lsj
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private TokenManager tokenManager;
    @Qualifier("UserDetailServiceImpl")
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    PhoneCodeSecurityConfig phoneCodeSecurityConfig;


    /**
     * .antMatchers("/swagger-ui/**",
     *                         "/webjars/**",
     *                         "/swagger-resources/**",
     *                         "/v3/**",
     *                         "/Visitor/**",
     *                         "/Static/**",
     *                         "/Alipay/**").permitAll();
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {


        http.authorizeRequests()
                .antMatchers("/swagger-ui/**",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/v3/**",
                        "/sysVisitor/**",
                        "/static/**",
                        "/view/**").permitAll();
        http.exceptionHandling()
                //没有权限访问
                .authenticationEntryPoint(new UnauthEntryPoint())
                .accessDeniedHandler(new SimpleAccessDeniedHandler())
                .and().cors().configurationSource(corsConfigurationSource())
                .and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/sysUser/**").hasAnyRole("0")
                .antMatchers("/sysAdmin/**").hasAnyRole("1","0")
                .anyRequest().authenticated()
                .and()
                .addFilter(new TokenLoginFilter(tokenManager, authenticationManager()))
                .addFilter(new TokenAuthFilter(authenticationManager(), tokenManager));
        //添加手机验证的securityConfig
        http.apply(phoneCodeSecurityConfig);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    /**
     * 密码加密对象
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
        corsConfiguration.setAllowedMethods(Arrays.asList("*"));
        corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
        corsConfiguration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",corsConfiguration);
        return source;
    }



}


