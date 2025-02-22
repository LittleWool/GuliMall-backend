package com.angenao.gulimall.product.web;

import com.angenao.gulimall.product.service.SkuInfoService;
import com.angenao.gulimall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

/**
 * @ClassName: ItemController
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/7 22:47
 * @Version: 1.0
 **/

@Controller
public class ItemController {

    @Autowired
    SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String item(@PathVariable Long skuId,Model model) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo=skuInfoService.getItemById(skuId);
        model.addAttribute("item",skuItemVo);
        return "item";
    }


}
