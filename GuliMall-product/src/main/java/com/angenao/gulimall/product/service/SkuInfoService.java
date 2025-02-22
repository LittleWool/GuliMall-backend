package com.angenao.gulimall.product.service;

import com.angenao.gulimall.product.entity.SpuInfoEntity;
import com.angenao.gulimall.product.vo.SkuItemVo;
import com.angenao.gulimall.product.vo.Skus;
import com.baomidou.mybatisplus.extension.service.IService;
import com.angenao.common.utils.PageUtils;
import com.angenao.gulimall.product.entity.SkuInfoEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 12:26:45
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);



    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    PageUtils queryPageByConditions(Map<String, Object> params);

    List<SkuInfoEntity> getSkusBySpuId(Long spuId);

    SkuItemVo getItemById(Long skuId) throws ExecutionException, InterruptedException;


    Map<Long, BigDecimal> getCuttentPrice(List<Long> ids);

    Map<Long,SkuInfoEntity> getSkusToMap(Set<Long> skuIds);
}

