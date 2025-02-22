package com.angenao.gulimall.order.feign;

import com.angenao.common.utils.R;
import com.angenao.gulimall.order.vo.FareVo;
import com.angenao.gulimall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @ClassName: WareFeignService
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/17 10:21
 * @Version: 1.0
 **/

@FeignClient("gulimall-ware")
public interface WareFeignService {
    @RequestMapping("/ware/waresku/hasStock")
    public R hasStock(@RequestBody List<Long> ids);


    @RequestMapping("/ware/wareinfo/fare/{addrId}")
    public FareVo getFare(@PathVariable("addrId") Long addrId);

    @PostMapping("/ware/waresku/lock/order")
    public R orderLock(@RequestBody WareSkuLockVo vo);
}
