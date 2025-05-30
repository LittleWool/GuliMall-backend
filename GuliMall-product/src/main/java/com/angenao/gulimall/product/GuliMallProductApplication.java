package com.angenao.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@MapperScan("com.angenao.gulimall.product.dao")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.angenao.gulimall.product.feign")
@EnableRedisHttpSession
public class GuliMallProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(GuliMallProductApplication.class, args);
	}

}
