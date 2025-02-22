package com.angenao.gulimall.coupon.service;

import com.angenao.common.to.SkuReductionTo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.angenao.common.utils.PageUtils;
import com.angenao.gulimall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 14:43:14
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void save(SkuReductionTo skuReductionTo);
}

