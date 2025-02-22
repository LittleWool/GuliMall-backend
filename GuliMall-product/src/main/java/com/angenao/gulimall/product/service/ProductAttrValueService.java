package com.angenao.gulimall.product.service;

import com.angenao.gulimall.product.vo.BaseAttrs;
import com.baomidou.mybatisplus.extension.service.IService;
import com.angenao.common.utils.PageUtils;
import com.angenao.gulimall.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 12:26:45
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void save(Long id, List<BaseAttrs> baseAttrs);

    List<ProductAttrValueEntity> baseListForSpu(Long spuId);

    void update(Long spuId, List<ProductAttrValueEntity> list);
}

