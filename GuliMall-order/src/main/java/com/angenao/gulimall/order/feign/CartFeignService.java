package com.angenao.gulimall.order.feign;

import com.angenao.gulimall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @ClassName: CartFeignService
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/16 23:55
 * @Version: 1.0
 **/

@FeignClient("gulimall-cart")
public interface CartFeignService {


    @ResponseBody
    @RequestMapping("/getCheckedItems")
    public List<OrderItemVo> getCheckedItems();
}
