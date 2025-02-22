package com.angenao.gulimall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@MapperScan("com.angenao.gulimall.member.dao")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.angenao.gulimall.member.feign")
@EnableRedisHttpSession
public class GuliMallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliMallMemberApplication.class, args);
    }

}
