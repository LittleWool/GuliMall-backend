package com.angenao.gulimall.product.feign;

import com.angenao.common.to.SkuHasStockTo;
import com.angenao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @ClassName: WareServiceFeign
 * @Description:
 * @Author: Little Wool
 * @Date: 2024/12/14 15:10
 * @Version: 1.0
 **/

@FeignClient("gulimall-ware")
public interface WareServiceFeign {


    @RequestMapping("ware/waresku/hasStock")
    public R hasStock(@RequestBody List<Long> ids);

}
