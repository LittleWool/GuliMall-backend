package com.angenao.gulimallcart.config;

import com.angenao.gulimallcart.interceptor.CartInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName: GuliMallWebMVCConfig
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/10 13:04
 * @Version: 1.0
 **/

@Configuration
public class GuliMallWebMVCConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CartInterceptor()).addPathPatterns("/**");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
