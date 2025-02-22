package com.angenao.gulimall.member.config;


import com.angenao.gulimall.member.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName: MyWebConfig
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/16 23:52
 * @Version: 1.0
 **/
@Configuration
public class MyWebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor());
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
