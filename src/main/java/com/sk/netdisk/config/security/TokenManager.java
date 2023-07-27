package com.sk.netdisk.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.sk.netdisk.pojo.security.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lsj
 * @description tokenmanager
 */
@Slf4j
@Component
public class TokenManager {

    /**
     * 当前日期
     */
    public static final int CALENDAR_FIELD = Calendar.DATE;
    /**
     * 设置token过期参数
     */
    public static final int CALENDAR_INTERVAL = 7;
    /**
     * 设置reFreshToken时间
     */
    public static final int RE_FRESH_TOKEN = 15;
    /**
     * 密钥
     */
    private final String tokenSignKey = "@1@2#1$!1:>(7)*24(%8*4(&6$4##45@2$%@#";


    /**
     * 生成token
     *
     * @param map
     * @return
     */
    public String createToken(Map<String, String> map) {
        Date iatDate = new Date();
        // expire time
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(CALENDAR_FIELD, CALENDAR_INTERVAL);
        Date expiresDate = nowTime.getTime();
        JWTCreator.Builder builder = JWT.create();
        // sign time
        builder.withIssuedAt(iatDate)
                // expire time
                .withExpiresAt(expiresDate);
        for (String key : map.keySet()) {
            builder = builder.withClaim(key, map.get(key));
        }
        String token = builder.sign(Algorithm.HMAC256(tokenSignKey));
        return token;
    }


    /**
     * 从Token中获得用户员工信息
     *
     * @return
     */
    public Map<String, String> getUserInfoFromToken(String token) {
        try {

            Map<String, String> map = new HashMap<>();
            String userId = JWT.decode(token).getClaim("userId").asString();
            String role = JWT.decode(token).getClaim("role").asString();
            map.put("userId",userId);
            map.put("role",role);
            return map;
        } catch (Exception e) {
            log.info("token格式不正确");
        }
        return null;
    }


    /**
     * 认证token是否正确
     *
     * @return
     */
    public boolean verfyToken(String token) {
        boolean flag = true;
        try {
            JWT.require(Algorithm.HMAC256(tokenSignKey)).build().verify(token);
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

}
