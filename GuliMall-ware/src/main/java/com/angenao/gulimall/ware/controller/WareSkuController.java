package com.angenao.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.angenao.common.to.SkuHasStockTo;
import com.angenao.gulimall.ware.vo.WareSkuLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.angenao.gulimall.ware.entity.WareSkuEntity;
import com.angenao.gulimall.ware.service.WareSkuService;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.R;



/**
 * 商品库存
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 15:25:46
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 批量查询是否有库存
     */
    @RequestMapping("/hasStock")
    public R hasStock(@RequestBody List<Long> ids){
      List<SkuHasStockTo> skuHasStockTos=wareSkuService.getHastStock(ids);
      return R.ok().put("data", skuHasStockTos);
    }

    /**
     * 将订单中的商品锁库存
     */
    @PostMapping("/lock/order")
    public R orderLock(@RequestBody WareSkuLockVo vo){
       boolean lock= wareSkuService.lockOrderStock(vo);
       return R.ok();
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
  //  @RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
