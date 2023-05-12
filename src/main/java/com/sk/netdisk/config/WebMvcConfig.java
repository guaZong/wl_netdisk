package com.sk.netdisk.config;

import com.sk.netdisk.interceptorl.MyInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * mvc拦截器
 * @author lsj
 */
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    MyInterceptor myInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(myInterceptor)
                //拦截的路径
                .addPathPatterns("/**")
                //放行的路径
                .excludePathPatterns("/","static");
    }
}
