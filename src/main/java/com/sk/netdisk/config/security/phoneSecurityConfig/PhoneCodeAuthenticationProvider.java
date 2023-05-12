package com.sk.netdisk.config.security.phoneSecurityConfig;

import com.sk.netdisk.constant.RedisConstants;
import com.sk.netdisk.util.Redis.RedisUtil;
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

@Component
public class PhoneCodeAuthenticationProvider implements AuthenticationProvider {
    public static final String FORM_PHONE_KEY = "username";
    public static final String FORM_PHONE_CODE_KEY = "code";

    private UserDetailsService userDetailsService;
    //需要先注入
    private RedisUtil redisUtil;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        authenticationChecks(authentication);
        String mobile = authentication.getName();
        UserDetails userDetails = userDetailsService.loadUserByUsername(mobile);
        PhoneAuthenticationToken authResult = new PhoneAuthenticationToken(userDetails, userDetails.getAuthorities());
        return authResult;
    }

    public PhoneCodeAuthenticationProvider(RedisUtil redisUtil) {
        this.redisUtil=redisUtil;
    }
    /**
     * 认证信息校验
     * @param authentication
     */
    private void authenticationChecks(Authentication authentication) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        // 表单提交的手机号和验证码
        String formMobile = request.getParameter(FORM_PHONE_KEY);
        String formSmsCode = request.getParameter(FORM_PHONE_CODE_KEY);
        // redis中保存的手机号和验证码
        String RedisPhoneCode = String.valueOf(redisUtil.get(RedisConstants.PHONE_LOGIN_KEY+formMobile));
        // 获取authentication参数的principal属性作为手机号
        if (StringUtils.isEmpty(RedisPhoneCode)) {
            throw new BadCredentialsException("未发送手机验证码");
        }
        if (!formSmsCode.equals(RedisPhoneCode)) {
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
