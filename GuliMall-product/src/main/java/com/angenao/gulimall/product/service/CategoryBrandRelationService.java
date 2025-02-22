package com.angenao.gulimall.product.service;

import com.angenao.common.valid.group.AddGroup;
import com.angenao.common.valid.group.UpdateGroup;
import com.angenao.gulimall.product.entity.BrandEntity;
import com.angenao.gulimall.product.vo.BrandVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.angenao.common.utils.PageUtils;
import com.angenao.gulimall.product.entity.CategoryBrandRelationEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 12:26:45
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);


    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    void updateBrand(@NotNull(message = "修改品牌id不能为空",groups = {UpdateGroup.class})
                     @Null(message = "新增id需要为空",groups = {AddGroup.class}) Long brandId,
                     @NotEmpty(message = "不能为空", groups = {AddGroup.class})
                     @NotBlank(message = "品牌名不能为空",groups = {AddGroup.class}) String name);

    void updateCategory(Long catId, String name);

    List<BrandEntity> getBrandsByCatId(long catId);
}

