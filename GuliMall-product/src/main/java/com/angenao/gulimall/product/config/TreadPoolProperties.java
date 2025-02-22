package com.angenao.gulimall.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName: TreadPoolProperties
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/8 11:04
 * @Version: 1.0
 **/

@ConfigurationProperties("gulimall.thread")
@Data
@Component
public class TreadPoolProperties {
     private int corePoolSize;
     private int maxPoolSize;
     private int queueCapacity;
     private int keepAliveSeconds;
}
