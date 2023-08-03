package com.netdisk.framework.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netdisk.common.constant.RedisConstants;
import com.netdisk.common.utils.Redis.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lsj
 * @description manger中的一项provider,验证用户提供的凭据(账号密码或者验证码)是否有效
 */
@Component
public class PhoneCodeAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    private final RedisUtil redisUtil;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //自定义的方法,进行校验手机验证码是否正确
        authenticationChecks(authentication);
        String mobile = authentication.getName();
        //验证码验证成功之后进行手机号认证,成功之后获取用户权限列表和用户id
        UserDetails userDetails = userDetailsService.loadUserByUsername(mobile);
        return new PhoneAuthenticationToken(userDetails, userDetails.getAuthorities());
    }
    public PhoneCodeAuthenticationProvider(RedisUtil redisUtil) {
        this.redisUtil=redisUtil;
    }
    /**
     * 自定义认证信息校验
     * @param authentication authentication
     */
    private void authenticationChecks(Authentication authentication) {
        Object username = authentication.getPrincipal();
        Object code=authentication.getCredentials();
        // redis中保存的手机号和验证码
        String redisPhoneCode = String.valueOf(redisUtil.get(RedisConstants.PHONE_LOGIN_KEY+username));
        // 获取authentication参数的principal属性作为手机号
        if (StringUtils.isEmpty(redisPhoneCode)) {
            throw new BadCredentialsException("未发送手机验证码");
        }
        if (!code.equals(redisPhoneCode)) {
            throw new BadCredentialsException("验证码不一致");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (PhoneAuthenticationToken.class.isAssignableFrom(authentication));
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}
