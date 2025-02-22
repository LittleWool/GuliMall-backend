package com.angenao.gulimall.product.service;

import com.angenao.gulimall.product.vo.Attr;
import com.angenao.gulimall.product.vo.SkuItemSaleAttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.angenao.common.utils.PageUtils;
import com.angenao.gulimall.product.entity.SkuSaleAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 12:26:45
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {



    PageUtils queryPage(Map<String, Object> params);

    void saveSaleAttr(Long skuId, List<Attr> attr);

    List<SkuItemSaleAttrVo> getSaleAttrBySpuId(Long spuId);

    List<String> getSkuSaleAttrValueAsString(long skuId);
}

