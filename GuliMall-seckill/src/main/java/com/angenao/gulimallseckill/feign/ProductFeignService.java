package com.angenao.gulimallseckill.feign;

import com.angenao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

/**
 * @ClassName: ProductFeignService
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/20 14:58
 * @Version: 1.0
 **/

@FeignClient("gulimall-product")
public interface ProductFeignService {

    @PostMapping("/product/skuinfo/getSkusToMap")
    // @RequiresPermissions("product:skuinfo:list")
    public R getSkusToMap(@RequestParam Set<Long> skuIds);
}
