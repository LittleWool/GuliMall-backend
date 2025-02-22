package com.angenao.gulimall.ware.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: MybatisPlusConfig
 * @Description:
 * @Author: Little Wool
 * @Date: 2024/11/26 10:50
 * @Version: 1.0
 **/

@Configuration
@MapperScan("com.angenao.gulimall.ware.dao")
public class MybatisPlusConfig {

    /**
     * 添加分页插件
     */
    @Bean
    public PaginationInterceptor mybatisPlusInterceptor() {
        PaginationInterceptor interceptor = new PaginationInterceptor();
        interceptor.setOverflow(true);
        interceptor.setLimit(100);
        return interceptor;
    }
}