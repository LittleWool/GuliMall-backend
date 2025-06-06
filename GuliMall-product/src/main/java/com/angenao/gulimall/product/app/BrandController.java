package com.angenao.gulimall.product.app;

import java.util.Arrays;
import java.util.Map;

import com.angenao.common.valid.group.AddGroup;
import com.angenao.common.valid.group.UpdateGroup;
import com.angenao.common.valid.group.UpdateStatusGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.angenao.gulimall.product.entity.BrandEntity;
import com.angenao.gulimall.product.service.BrandService;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.R;


/**
 * 品牌
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 12:26:45
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
  //  @RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:brand:save")
    public R save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand){
//    public R save(@Valid @RequestBody BrandEntity brand, BindingResult bindingResult){
//        if (bindingResult.hasErrors()){
//            Map errors=new HashMap();
//            bindingResult.getFieldErrors().forEach(fieldError -> {
//                String field = fieldError.getField();
//                errors.put(field, fieldError.getDefaultMessage());
//            });
//            return R.error(400,"提交数据不合法").put("data", errors);
//        }else {
//
//        }

        brandService.save(brand);
        return R.ok();

    }

    /**
     * 修改
     */
    @Transactional
    @RequestMapping("/update")
    //@RequiresPermissions("product:brand:update")
    public R update(@Validated({UpdateGroup.class})@RequestBody BrandEntity brand){
		brandService.updateById(brand);
        brandService.updateCascade(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update/status")
    //@RequiresPermissions("product:brand:update")
    public R updateStatus(@Validated({UpdateStatusGroup.class})@RequestBody BrandEntity brand){
        brandService.updateById(brand);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
