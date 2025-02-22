package com.angenao.gulimall.order.feign;

import com.angenao.gulimall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @ClassName: MemberFeignService
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/16 23:54
 * @Version: 1.0
 **/

@FeignClient("gulimall-member")
public interface MemberFeignService {

    @RequestMapping("/member/memberreceiveaddress/user/{userId}")
    List<MemberAddressVo> getReceiveAddresses(@PathVariable("userId") long userId);
}
