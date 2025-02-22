package com.angenao.gulimall.thirdpart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class GuliMallThirdPartApplication {


    public static void main(String[] args) {
        SpringApplication.run(GuliMallThirdPartApplication.class, args);
    }



}
