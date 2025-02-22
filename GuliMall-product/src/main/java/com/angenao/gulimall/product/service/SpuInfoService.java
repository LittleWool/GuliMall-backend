package com.angenao.gulimall.product.service;

import com.angenao.gulimall.product.to.SpuInfoTo;
import com.angenao.gulimall.product.vo.SpuSaveVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.angenao.common.utils.PageUtils;
import com.angenao.gulimall.product.entity.SpuInfoEntity;

import java.util.List;
import java.util.Map;

/**
 * spu信息
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 12:26:45
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void save(SpuSaveVo vo);

    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);

    PageUtils queryPageByConditions(Map<String, Object> params);


    void up(Long spuId);


    Map<Long,SpuInfoEntity>  getSpuBySkuIds(List<Long> skuIds);
}

