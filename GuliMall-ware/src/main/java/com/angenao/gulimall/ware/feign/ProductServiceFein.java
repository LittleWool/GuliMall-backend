package com.angenao.gulimall.ware.feign;

import com.angenao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName: ProductServiceFein
 * @Description:
 * @Author: Little Wool
 * @Date: 2024/12/11 10:44
 * @Version: 1.0
 **/

@FeignClient("gulimall-product")
public interface ProductServiceFein {

    @RequestMapping("/product/skuinfo/info/{id}")
    public R info(@PathVariable("id") Long id);

}
