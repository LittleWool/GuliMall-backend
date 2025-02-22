package com.angenao.gulimall.product.feign;

import com.angenao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @ClassName: SeckillFeignService
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/20 18:57
 * @Version: 1.0
 **/
@FeignClient("gulimall-seckill")
public interface SeckillFeignService {

    @PostMapping("/seckill/getSeckillInfo/{id}")
    public R getSecKillInfo(@PathVariable(value = "id") Long skuId);
}

