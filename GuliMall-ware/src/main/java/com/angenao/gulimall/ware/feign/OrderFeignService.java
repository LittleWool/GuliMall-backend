package com.angenao.gulimall.ware.feign;

import com.angenao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName: OrderFeignService
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/19 15:40
 * @Version: 1.0
 **/
@FeignClient("gulimall-order")
public interface OrderFeignService {
    @RequestMapping("/order/order/info/byOrderSn")
    R getByOrderSn(@RequestParam String orderSn);
}
