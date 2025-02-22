package com.angenao.gulimallseckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableFeignClients
@EnableRedisHttpSession
public class GuliMallSeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliMallSeckillApplication.class, args);
    }

}
