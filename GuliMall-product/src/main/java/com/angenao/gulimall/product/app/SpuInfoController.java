package com.angenao.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.angenao.gulimall.product.to.SpuInfoTo;
import com.angenao.gulimall.product.vo.SpuSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.angenao.gulimall.product.entity.SpuInfoEntity;
import com.angenao.gulimall.product.service.SpuInfoService;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.R;



/**
 * spu信息
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 12:26:45
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("product:spuinfo:list")
    public R list(@RequestParam Map<String, Object> params){

        PageUtils page = spuInfoService.queryPageByConditions(params);
        return R.ok().put("page", page);
    }

    @PostMapping("/list")
    public R getSpuBySkuIds(@RequestBody List<Long> skuIds){
        Map<Long, SpuInfoEntity> spuBySkuIds = spuInfoService.getSpuBySkuIds(skuIds);
        return R.ok().setData(spuBySkuIds);
    }


    @PostMapping("/{spuId}/up")
    public R up(@PathVariable Long spuId){
        spuInfoService.up(spuId);
        return R.ok();
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
  //  @RequiresPermissions("product:spuinfo:info")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:spuinfo:save")
    public R save(@RequestBody SpuSaveVo vo){
		spuInfoService.save(vo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:spuinfo:update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:spuinfo:delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
