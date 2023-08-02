package com.sk.netdisk.config.security.phoneSecurityConfig;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author lsj
 * @description 封装用于手机验证码登录的PhoneAuthenticationToken,相当于UsernamePasswordAuthentication
 */
public class PhoneAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;

    private final Object credentials;

    /**
     * 账号未认证
     * @param principal 账号
     * @param credentials 密码
     */
    public PhoneAuthenticationToken(Object principal, Object credentials) {
        //无权限列表
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        //设置账号还没有进行验证码验证,接下来需要验证
        setAuthenticated(false);
    }

    /**
     * 账号已经认证成功
     * @param principal 账号
     * @param authorities 权限列表
     */
    public PhoneAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        //有权限列表
        super(authorities);
        this.principal = principal;
        this.credentials = null;
        super.setAuthenticated(true);
    }

    /**
     * 获取验证码
     * @return Object 返回验证码
     */
    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    /**
     * 获取手机号
     * @return Object 手机号
     */
    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
