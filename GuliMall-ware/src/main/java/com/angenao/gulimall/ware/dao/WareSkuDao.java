package com.angenao.gulimall.ware.dao;

import com.angenao.common.to.SkuHasStockTo;
import com.angenao.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 15:25:46
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void addStock(@Param("skuId") Long skuId,@Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    List<SkuHasStockTo> getHasStock(@Param("ids") List<Long> ids);

    List<Long> listWareIdsHasStock(@Param("skuId") Long skuId, @Param("count") Integer count);

    int tryLockStock(@Param("skuId") Long skuId,@Param("count") Integer count,@Param("wareId") Long wareId);

    void unlockStock(@Param(("skuId")) Long skuId,@Param("skuNum") Integer skuNum,@Param("wareId") Long wareId);
}
