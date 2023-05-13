package com.sk.netdisk.config.security.phoneSecurityConfig;

import com.sk.netdisk.config.security.TokenManager;
import com.sk.netdisk.pojo.security.SecurityPhone;
import com.sk.netdisk.util.Redis.RedisUtil;
import com.sk.netdisk.util.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

@Slf4j
public class PhoneSuccessHandler implements AuthenticationSuccessHandler {

    TokenManager tokenManager;
    RedisUtil redisUtil;
    public PhoneSuccessHandler(TokenManager tokenManager,RedisUtil redisUtil){
        this.tokenManager=tokenManager;
        this.redisUtil=redisUtil;
    }
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        SecurityPhone admin = (SecurityPhone) authentication.getPrincipal();
        // 保存用户信息到redis中
        // 用户根据验证码进行登录-生成token
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("userId",admin.getUserId().toString());
        hashMap.put("role",admin.getPermissionValueList().get(0));
        String token=tokenManager.createToken(hashMap);
        String refreshToken = tokenManager.createToken(hashMap);
        // 存储token 和对象数据在redis中-过期值一天
        redisUtil.hmset(token,hashMap,60*60*24);
        // 返回token
        HashMap<String, String> result = new HashMap<>();
        result.put("token", token);
        result.put("refresh_token",refreshToken);
        log.info(admin.getUsername() + ":管理员手机登录成功   时间：" + LocalDateTime.now());
        ResponseResult.out(response,ResponseResult.success(result));
    }
}
