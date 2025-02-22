package com.angenao.gulimall.product.feign;

import com.angenao.common.to.SkuReductionTo;
import com.angenao.common.to.SpuBoundTo;
import com.angenao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName: CouponServiceFeign
 * @Description:
 * @Author: Little Wool
 * @Date: 2024/12/9 20:35
 * @Version: 1.0
 **/

@FeignClient("gulimall-coupon")
public interface CouponServiceFeign {

    @PostMapping("/coupon/spubounds/save")
    R save(@RequestBody SpuBoundTo spuBoundTo);

    @PostMapping("/coupon/skufullreduction/saveInfo")
    R saveSkuReduction(SkuReductionTo skuReductionTo);
}
