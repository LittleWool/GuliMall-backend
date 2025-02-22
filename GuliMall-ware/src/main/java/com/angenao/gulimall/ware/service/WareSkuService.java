package com.angenao.gulimall.ware.service;

import com.angenao.common.mq.OrderTo;
import com.angenao.common.mq.StockLockedTo;
import com.angenao.common.to.SkuHasStockTo;
import com.angenao.gulimall.ware.vo.WareSkuLockVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.angenao.common.utils.PageUtils;
import com.angenao.gulimall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 15:25:46
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockTo> getHastStock(List<Long> ids);

    boolean lockOrderStock(WareSkuLockVo vo);

    void unLock(StockLockedTo stockLockedTo);
    void unLock(OrderTo orderTo);

}

