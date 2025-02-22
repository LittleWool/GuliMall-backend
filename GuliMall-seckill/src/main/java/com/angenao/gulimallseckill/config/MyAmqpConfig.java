package com.angenao.gulimallseckill.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: MyAmqpConfig
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/21 15:25
 * @Version: 1.0
 **/
@Configuration
public class MyAmqpConfig {

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
