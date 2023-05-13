package com.sk.netdisk.config.security.phoneSecurityConfig;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author LSJ
 */
public class PhoneAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;

    private Object credentials;

    /**
     * 未认证
     * @param principal
     * @param credentials
     */
    public PhoneAuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(false);
    }
    /**
     * 已经认证成功
     */

    public PhoneAuthenticationToken(Object principal, Object credentials,
                                    Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

    /**
     * 获取验证码
     * @return
     */
    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    /**
     * 获取手机号
     * @return
     */
    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
