package com.angenao.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.angenao.gulimall.ware.vo.MergeVo;
import com.angenao.gulimall.ware.vo.PurchaseDoneVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.angenao.gulimall.ware.entity.PurchaseEntity;
import com.angenao.gulimall.ware.service.PurchaseService;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.R;



/**
 * 采购信息
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 15:25:46
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("ware:purchase:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }

    @GetMapping("/unreceive/list")
    public R getUnreceiveList(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPageUnreceiveList(params);
        return R.ok().put("page", page);
    }

    /**
     * 领取采购单
     * @param ids
     * @return
     */
    @PostMapping("/received")
    public R receive(@RequestBody List<Long> ids){
        purchaseService.receive(ids);
        return R.ok();
    }

    /**
     * 完成采购单
     * @param purchaseDoneVO
     * @return
     */
    @PostMapping("/done")
    public R done(@RequestBody PurchaseDoneVO purchaseDoneVO){
        purchaseService.done(purchaseDoneVO);
        return R.ok();
    }

    /**
     * 合并
     */
    @PostMapping("/merge")
    public R mergePurchageList(@RequestBody MergeVo mergeVo){
        purchaseService.mergePurchaseList(mergeVo);
        return R.ok();
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
  //  @RequiresPermissions("ware:purchase:info")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase){
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:purchase:delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
