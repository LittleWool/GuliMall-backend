package com.angenao.gulimall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@MapperScan("com.angenao.gulimall.coupon.dao")
@EnableDiscoveryClient
public class GuliMallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliMallCouponApplication.class, args);
    }

}
