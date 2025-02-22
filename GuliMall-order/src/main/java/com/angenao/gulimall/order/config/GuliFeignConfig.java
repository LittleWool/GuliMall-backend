package com.angenao.gulimall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: GuliFeignConfig
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/17 7:55
 * @Version: 1.0
 **/
@Configuration
public class GuliFeignConfig {

    //将请求头cookie放入远程请求头中，解决远程调用请求头丢失
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
                if (requestAttributes != null) {
                    HttpServletRequest request = requestAttributes.getRequest();
                    String cookie = request.getHeader("Cookie");
                    if (cookie != null) {
                        template.header("Cookie", cookie);
                    }
                }

            }
        };
    }
}
