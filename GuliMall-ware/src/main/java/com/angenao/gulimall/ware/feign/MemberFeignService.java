package com.angenao.gulimall.ware.feign;

import com.angenao.common.utils.R;
import com.angenao.gulimall.ware.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @ClassName: MemberFeignService
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/17 13:14
 * @Version: 1.0
 **/

@FeignClient("gulimall-member")
public interface MemberFeignService {
    @RequestMapping("/member/memberreceiveaddress/{addrId}")
    R getReceiveAddressByAddrId(@PathVariable("addrId") long addrId);
}
