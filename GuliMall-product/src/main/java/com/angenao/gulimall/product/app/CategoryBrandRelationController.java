package com.angenao.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.angenao.gulimall.product.entity.BrandEntity;
import com.angenao.gulimall.product.vo.BrandVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.angenao.gulimall.product.entity.CategoryBrandRelationEntity;
import com.angenao.gulimall.product.service.CategoryBrandRelationService;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 12:26:45
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("product:categorybrandrelation:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }
    @GetMapping("/catelog/list")
   // @RequiresPermissions("product:categorybrandrelation:list")
    public R cateloglist(@RequestParam long brandId){
        List<CategoryBrandRelationEntity> brandEnttities = categoryBrandRelationService.
                list(new QueryWrapper<CategoryBrandRelationEntity>().
                        eq("brand_id", brandId));
        return R.ok().put("data", brandEnttities);
    }

    /**
     * 查询与三级分类相关的品牌列表
     * @param brandId
     * @return
     */
    @GetMapping("/brands/list")
    public R brandslist(@RequestParam(value = "catId" ,required = true) long catId){
       List<BrandEntity> brandEntities=categoryBrandRelationService.getBrandsByCatId(catId);
        List<BrandVO> brandVOS = brandEntities.stream().map((brand) -> {
            BrandVO brandVO = new BrandVO();
            brandVO.setBrandId(brand.getBrandId());
            brandVO.setBrandName(brand.getName());
            return brandVO;
        }).collect(Collectors.toList());
        return R.ok().put("data", brandVOS);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
  //  @RequiresPermissions("product:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:categorybrandrelation:save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.saveDetail(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
