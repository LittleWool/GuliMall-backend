package com.angenao.gulimall.product.service;

import com.angenao.gulimall.product.vo.Images;
import com.baomidou.mybatisplus.extension.service.IService;
import com.angenao.common.utils.PageUtils;
import com.angenao.gulimall.product.entity.SkuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * sku图片
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 12:26:45
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void save(Long skuId, List<Images> images);

    List<SkuImagesEntity> getImagesBySkuId(Long skuId);
}

