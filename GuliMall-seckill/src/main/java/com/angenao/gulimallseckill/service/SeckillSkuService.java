package com.angenao.gulimallseckill.service;

import com.angenao.common.to.SkuReductionTo;
import com.angenao.gulimallseckill.to.SeckillSkuRedisTo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: SeckillSkuService
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/20 14:04
 * @Version: 1.0
 **/

@Service
public interface SeckillSkuService {

    /**
     * 上架未来三天秒杀商品
     */
    void uploadSecKillSkuLatest3Days();

    List<SeckillSkuRedisTo> getCurrentSeckillSkus();

    SeckillSkuRedisTo getSeckillInfo(Long skuId);

    String kill(String killId, String randomCode, Integer num) throws InterruptedException;
}
