package com.angenao.gulimall.product.config;

import com.angenao.gulimall.product.constants.ProductConfigConstant;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;


/**
 * @ClassName: MyRedissionCOnfig
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/4 17:35
 * @Version: 1.0
 **/

@Configuration
public class MyRedissionConfig {

    @Bean
    public RedissonClient redissonClient() throws IOException {
        Config config = new Config();
        config.useSingleServer().setAddress(ProductConfigConstant.REDISSION_CONFIG_ADDRESS_SINGLE);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
