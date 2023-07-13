package com.sk.netdisk.config;

import com.sk.netdisk.interceptorl.MyInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * mvc拦截器
 * @author lsj
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    MyInterceptor myInterceptor;

    /**
     * 定义三层资源,第一层是visitor可以随意访问,
     * 第二层是用户可以访问自己拥有的资源,
     * 第三层是管理员可以访问所有资源,通过路径访问的只有visitor和admin能访问,其他都限制
     */
    private final String filePath = System.getProperty("user.dir")+"/static/";


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/view/**").addResourceLocations("file:"+filePath+"view/");
        registry.addResourceHandler("/img/**").addResourceLocations("file:"+filePath+"img/");
        registry.addResourceHandler("/video/**").addResourceLocations("file:"+filePath+"video/");
        registry.addResourceHandler("/doc/**").addResourceLocations("file:"+filePath+"doc/");
        registry.addResourceHandler("/music/**").addResourceLocations("file:"+filePath+"music/");
        registry.addResourceHandler("/torrent/**").addResourceLocations("file:"+filePath+"torrent/");
        registry.addResourceHandler("/zip/**").addResourceLocations("file:"+filePath+"zip/");
        registry.addResourceHandler("/other/**").addResourceLocations("file:"+filePath+"other/");
    }


}
