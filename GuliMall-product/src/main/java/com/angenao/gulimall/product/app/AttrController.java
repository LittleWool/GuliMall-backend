package com.angenao.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.angenao.gulimall.product.entity.ProductAttrValueEntity;
import com.angenao.gulimall.product.service.impl.ProductAttrValueServiceImpl;
import com.angenao.gulimall.product.vo.AttrResVo;
import com.angenao.gulimall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.angenao.gulimall.product.service.AttrService;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.R;



/**
 * 商品属性
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 12:26:45
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;
    @Autowired
    private ProductAttrValueServiceImpl productAttrValueService;

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }

    @GetMapping("/base/listforspu/{spuId}")
    public R listforspu(@PathVariable("spuId") Long spuId){
        List<ProductAttrValueEntity> list=productAttrValueService.baseListForSpu(spuId);
        return R.ok().put("data", list);
    }

    @PostMapping("/update/{spuId}")
    public R update(@PathVariable("spuId") Long spuId,@RequestBody List<ProductAttrValueEntity> list){
        productAttrValueService.update(spuId,list);
        return R.ok();
    }


    //base/list/{catelogId}
    @RequestMapping("/{attrType}/list/{catelogId}")
    public R productAttrBaseList(@RequestParam Map<String,Object> map,@PathVariable("catelogId") Long catelogId,@PathVariable("attrType") String attrType){
        PageUtils page= attrService.queryBaseAttrPage(map,catelogId,attrType);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
  //  @RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
//		AttrEntity attr = attrService.getById(attrId);
        AttrResVo attrResVo=attrService.getAttrInfo(attrId);

        return R.ok().put("attr", attrResVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attrVo){
		attrService.saveAttrVo(attrVo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attrVo){
//		attrService.updateById(attr);
        attrService.updateCascade(attrVo);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
