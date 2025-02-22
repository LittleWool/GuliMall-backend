package com.angenao.gulimall.member.feign;

import com.angenao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName: CouponFeign
 * @Description:
 * @Author: Little Wool
 * @Date: 2024/11/14 20:46
 * @Version: 1.0
 **/

@Component
@FeignClient("GuliMall-coupon")
public interface CouponFeign {
    @RequestMapping("/coupon/coupon/coupons")
    public R coupons();
}
