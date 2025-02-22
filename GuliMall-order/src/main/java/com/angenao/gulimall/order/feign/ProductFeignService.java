package com.angenao.gulimall.order.feign;

import com.angenao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @ClassName: ProductFeignService
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/17 20:26
 * @Version: 1.0
 **/

@FeignClient("gulimall-product")
public interface ProductFeignService {
    @PostMapping("/product/spuinfo/list")
     R getSpuBySkuIds(@RequestBody List<Long> skuIds);



    @RequestMapping("/product/skuinfo/info/{skuId}")
    R info(@PathVariable("skuId") Long skuId);
}
