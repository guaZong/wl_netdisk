package com.sk.netdisk.util;

import com.sk.netdisk.enums.AppExceptionCodeMsg;
import com.sk.netdisk.exception.AppException;
import com.sk.netdisk.pojo.security.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 用户工具类
 *
 * @author lsj
 */
@Slf4j
public class UserUtil {
    /**
     * 获取登录用户的id
     *
     * @return 登录用户id
     */
    public static Integer getLoginUserId() {
        try {
            SecurityContext sc = SecurityContextHolder.getContext();
            Authentication auth = sc.getAuthentication();
            SecurityUser securityUser = (SecurityUser) auth.getPrincipal();
            if(securityUser.getUserId()==null){
                throw new AppException(AppExceptionCodeMsg.USER_NOT_LOGIN);
            }
            return securityUser.getUserId();
        }catch (Exception e){
            throw new AppException(AppExceptionCodeMsg.USER_NOT_LOGIN);
        }
    }
}
