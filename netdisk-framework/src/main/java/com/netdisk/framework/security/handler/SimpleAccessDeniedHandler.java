package com.netdisk.framework.security.handler;





import com.netdisk.common.enums.AppExceptionCodeMsg;
import com.netdisk.common.exception.AppException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Administrator
 * @description 密码登录失败处理器
 */
public class SimpleAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
       throw new AppException(AppExceptionCodeMsg.INVALID_PERMISSION);
    }
}
