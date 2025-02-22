package com.angenao.gulimall.product.service.impl;

import com.angenao.gulimall.product.vo.Images;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.Query;

import com.angenao.gulimall.product.dao.SkuImagesDao;
import com.angenao.gulimall.product.entity.SkuImagesEntity;
import com.angenao.gulimall.product.service.SkuImagesService;


@Service("skuImagesService")
public class SkuImagesServiceImpl extends ServiceImpl<SkuImagesDao, SkuImagesEntity> implements SkuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuImagesEntity> page = this.page(
                new Query<SkuImagesEntity>().getPage(params),
                new QueryWrapper<SkuImagesEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void save(Long skuId, List<Images> images) {
        List<SkuImagesEntity> collect = images.stream().map((image -> {
            SkuImagesEntity imagesEntity = new SkuImagesEntity();
            imagesEntity.setSkuId(skuId);
            imagesEntity.setImgUrl(image.getImgUrl());
            imagesEntity.setDefaultImg(image.getDefaultImg());
            return imagesEntity;
        })).filter(skuImagesEntity -> {
            return StringUtils.isNotEmpty(skuImagesEntity.getImgUrl());
        }).collect(Collectors.toList());
        this.saveBatch(collect);
    }

    @Override
    public List<SkuImagesEntity> getImagesBySkuId(Long skuId) {

        SkuImagesDao skuImagesDao = this.getBaseMapper();
        List<SkuImagesEntity> res=skuImagesDao.selectList(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
        return res;
    }

}