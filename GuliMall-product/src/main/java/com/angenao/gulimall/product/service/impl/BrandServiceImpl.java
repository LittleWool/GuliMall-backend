package com.angenao.gulimall.product.service.impl;

import com.angenao.gulimall.product.dao.CategoryBrandRelationDao;
import com.angenao.gulimall.product.entity.CategoryBrandRelationEntity;
import com.angenao.gulimall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.Query;

import com.angenao.gulimall.product.dao.BrandDao;
import com.angenao.gulimall.product.entity.BrandEntity;
import com.angenao.gulimall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;
    @Override
    public PageUtils queryPage(Map<String, Object> params ) {
        QueryWrapper<BrandEntity> brandEntityQueryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            brandEntityQueryWrapper.and(t -> t.eq("brand_id", key).or().like("name", key));
        }
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                brandEntityQueryWrapper
        );
        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void updateCascade(BrandEntity brand) {
        this.updateById(brand);
        if (!StringUtils.isEmpty(brand.getName())) {
            categoryBrandRelationService.updateBrand(brand.getBrandId(),brand.getName());
        }
    }

}