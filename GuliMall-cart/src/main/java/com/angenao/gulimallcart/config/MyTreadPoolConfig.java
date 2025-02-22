package com.angenao.gulimallcart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: MyTreadPoolConfig
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/8 11:06
 * @Version: 1.0
 **/


@Configuration
public class MyTreadPoolConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(TreadPoolProperties poolProperties) {
        ThreadPoolExecutor threadPoolExecutor=new ThreadPoolExecutor(
                poolProperties.getCorePoolSize(),
                poolProperties.getMaxPoolSize(),
                poolProperties.getKeepAliveSeconds(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(poolProperties.getQueueCapacity()),
                new ThreadPoolExecutor.AbortPolicy()

        );
        return threadPoolExecutor;
    }
}
