package com.angenao.gulimall.search.controller;

import com.angenao.common.exception.BizCode;
import com.angenao.common.to.es.SkuEsModel;
import com.angenao.common.utils.R;
import com.angenao.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @ClassName: ElasticSaveController
 * @Description:
 * @Author: Little Wool
 * @Date: 2024/12/14 16:13
 * @Version: 1.0
 **/

@Slf4j
@RequestMapping("search/save")
@RestController()
public class ElasticSaveController {

    @Autowired
    ProductSaveService productSaveService;

    //上架商品
    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels) {
        boolean b = false;
        try {
            b=productSaveService.productStatusUp(skuEsModels);
        } catch (IOException e) {
            log.error("ElasticSearch商品上架错误:{}", e);
            return R.error(BizCode.PRODUCT_UP_EXCEPTION.getCode(), BizCode.PRODUCT_UP_EXCEPTION.getMsg());
        }
        if (b){
            return R.ok();
        }else {
            return R.error(BizCode.PRODUCT_UP_EXCEPTION.getCode(), BizCode.PRODUCT_UP_EXCEPTION.getMsg());
        }
    }
}
