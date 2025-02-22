package com.angenao.gulimallseckill.scheduled;

import com.angenao.gulimallseckill.constant.SeckillConstant;
import com.angenao.gulimallseckill.service.SeckillSkuService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @ClassName: SeckillSkuScheduled
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/20 14:03
 * @Version: 1.0
 **/

@Slf4j
@Service
public class SeckillSkuScheduled {


    @Autowired
    SeckillSkuService seckillSkuService;

    @Autowired
    RedissonClient redissonClient;
    /**
     * 上架未来三天的秒杀商品
     */
    @Scheduled(cron = "0 * * * * ?")
    public void uploadSeckillSkuLatest3Days() {
        RLock lock = redissonClient.getLock(SeckillConstant.SECKILL_UPLOAD_REDIS_LOCK);
        lock.lock();
        seckillSkuService.uploadSecKillSkuLatest3Days();
        lock.unlock();
    }

}
