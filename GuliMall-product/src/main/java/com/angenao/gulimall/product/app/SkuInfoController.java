package com.angenao.gulimall.product.app;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.angenao.gulimall.product.entity.SkuInfoEntity;
import com.angenao.gulimall.product.service.SkuInfoService;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.R;


/**
 * sku信息
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 12:26:45
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:skuinfo:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = skuInfoService.queryPageByConditions(params);

        return R.ok().put("page", page);
    }

    @PostMapping("/getSkusToMap")
    // @RequiresPermissions("product:skuinfo:list")
    public R getSkusToMap(@RequestParam Set<Long> skuIds) {
        Map<Long,SkuInfoEntity> res = skuInfoService.getSkusToMap(skuIds);

        return R.ok().setData(res);
    }

    //查询一批商品当前价格
    @RequestMapping("/product/skuprices")
    Map<Long, BigDecimal> getCurrentPrice(@RequestBody List<Long> ids) {
        Map<Long, BigDecimal> res = skuInfoService.getCuttentPrice(ids);
        return res;
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{skuId}")
    //  @RequiresPermissions("product:skuinfo:info")
    public R info(@PathVariable("skuId") Long skuId) {
        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R.ok().put("skuInfo", skuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:skuinfo:save")
    public R save(@RequestBody SkuInfoEntity skuInfo) {
        skuInfoService.save(skuInfo);

        return R.ok();
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:skuinfo:update")
    public R update(@RequestBody SkuInfoEntity skuInfo) {
        skuInfoService.updateById(skuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:skuinfo:delete")
    public R delete(@RequestBody Long[] skuIds) {
        skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

}
