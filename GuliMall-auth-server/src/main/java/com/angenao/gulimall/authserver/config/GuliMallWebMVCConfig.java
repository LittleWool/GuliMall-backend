package com.angenao.gulimall.authserver.config;

import org.springframework.context.annotation.Configuration;
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
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/reg.html").setViewName("reg");
        WebMvcConfigurer.super.addViewControllers(registry);
    }
}
