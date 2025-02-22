package com.angenao.gulimall.product.feign;

import com.angenao.common.to.es.SkuEsModel;
import com.angenao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @ClassName: SearchServiceFeign
 * @Description:
 * @Author: Little Wool
 * @Date: 2024/12/15 16:13
 * @Version: 1.0
 **/

@FeignClient("gulimall-search")
public interface SearchServiceFeign {

    @PostMapping("/search/save/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
