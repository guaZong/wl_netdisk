package com.netdisk.framework.security.handler;


import com.netdisk.common.utils.Redis.RedisUtil;
import com.netdisk.common.core.ResponseResult;
import com.netdisk.common.core.domain.SecurityPhone;
import com.netdisk.framework.web.TokenManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
/**
 * @author lsj
 * @description 手机登录成功处理器
 */
@Slf4j
public class PhoneSuccessHandler implements AuthenticationSuccessHandler {

    TokenManager tokenManager;
    RedisUtil redisUtil;
    public PhoneSuccessHandler(TokenManager tokenManager,RedisUtil redisUtil){
        this.tokenManager=tokenManager;
        this.redisUtil=redisUtil;
    }
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse response, Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        SecurityPhone phone = (SecurityPhone) authentication.getPrincipal();
        // 保存用户信息到redis中
        // 用户根据验证码进行登录-生成token
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("userId",phone.getUserId().toString());
        hashMap.put("role",phone.getPermissionValueList().get(0));
        String token=tokenManager.createToken(hashMap);
        String refreshToken = tokenManager.createToken(hashMap);
        // 存储token 和对象数据在redis中-过期值一天
        redisUtil.hmset(token,hashMap,60*60*24);
        // 返回token
        HashMap<String, String> result = new HashMap<>();
        result.put("token", token);
        result.put("refresh_token",refreshToken);
        log.info(phone.getUsername() + ":管理员手机登录成功   时间：" + LocalDateTime.now());
        ResponseResult.out(response,ResponseResult.success(result));
    }
}
