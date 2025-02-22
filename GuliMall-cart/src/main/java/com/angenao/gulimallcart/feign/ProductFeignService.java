package com.angenao.gulimallcart.feign;

import com.angenao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ProductFeign
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/14 17:26
 * @Version: 1.0
 **/
@FeignClient("gulimall-product")
public interface ProductFeignService {

    //sku基本信息
    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);

    //sku销售属性
    @RequestMapping("/product/skusaleattrvalue/getSkuSaleAttrValuesAsString")
    public List<String> getSkuSaleAttrValuesAsString(@RequestParam long skuId);


    @RequestMapping("/product/skuinfo/product/skuprices")
    Map<Long, BigDecimal> getCurrentPrice(List<Long> ids);
}
