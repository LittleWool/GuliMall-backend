package com.angenao.gulimallseckill.feign;

import com.angenao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @ClassName: CouponFeignService
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/20 14:08
 * @Version: 1.0
 **/

@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    @PostMapping("/coupon/seckillsession/getSeckillSessionsIn3Days")
    public R getSessionsIn3Days();

}
