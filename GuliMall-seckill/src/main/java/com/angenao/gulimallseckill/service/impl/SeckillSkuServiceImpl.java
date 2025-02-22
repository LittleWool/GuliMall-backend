package com.angenao.gulimallseckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.angenao.common.mq.SeckillOrderTo;
import com.angenao.common.utils.R;
import com.angenao.common.vo.MemberVo;
import com.angenao.gulimallseckill.constant.SeckillConstant;
import com.angenao.gulimallseckill.feign.CouponFeignService;
import com.angenao.gulimallseckill.feign.ProductFeignService;
import com.angenao.gulimallseckill.interceptor.LoginInterceptor;
import com.angenao.gulimallseckill.service.SeckillSkuService;
import com.angenao.gulimallseckill.to.SeckillSessionTo;
import com.angenao.gulimallseckill.to.SeckillSkuRedisTo;
import com.angenao.gulimallseckill.to.SeckillSkuTo;
import com.angenao.gulimallseckill.vo.SkuInfoVo;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @ClassName: SeckillSkuServiceimpl
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/20 14:04
 * @Version: 1.0
 **/

@Slf4j
@Service
public class SeckillSkuServiceImpl implements SeckillSkuService {

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private LoginInterceptor loginInterceptor;


    @Override
    public void uploadSecKillSkuLatest3Days() {
        log.info("上架秒杀商品");
        //幂等性，加一个分布式锁
        R r = couponFeignService.getSessionsIn3Days();
        if (r.getCode() == 0) {
            List<SeckillSessionTo> seckillSessions = r.getData(new TypeReference<List<SeckillSessionTo>>() {
            });

            //将session存入redis
            saveSessionInfos(seckillSessions);
            //将相关商品存入redis
            saveSessionSkuInfos(seckillSessions);

        }
    }

    /**
     * 获得当前正在秒杀的商品
     *
     * @return
     */


    //获取当前秒杀活动，一段时间内只有一个活动
    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
        Set<String> keys = stringRedisTemplate.keys(SeckillConstant.SECKILL_SESSION_REDIS_PREFIX + "*");
        for (String key : keys) {
            String replace = key.replace(SeckillConstant.SECKILL_SESSION_REDIS_PREFIX, "");
            String[] duration = replace.split("_");
            long currentTime = System.currentTimeMillis();
            long startTime = Long.parseLong(duration[0]);
            long endTime = Long.parseLong(duration[1]);
            //该场活动在时间范围内
            if (currentTime >= startTime && currentTime <= endTime) {
                //场次_skuId
                List<String> range = stringRedisTemplate.opsForList().range(key, 0, -1);
                BoundHashOperations<String, String, Object> ops = stringRedisTemplate.boundHashOps(SeckillConstant.SECKILL_SESSIONSKUS_REDIS_PREFIX);
                List<Object> skus = ops.multiGet(range);
                if (skus != null) {
                    List<SeckillSkuRedisTo> res = skus.stream().map(jsonString -> {
                        SeckillSkuRedisTo seckillSkuRedisTo = JSON.parseObject(jsonString.toString(), SeckillSkuRedisTo.class);
                        return seckillSkuRedisTo;
                    }).collect(Collectors.toList());
                    return res;
                }

            }
        }
        return null;
    }

    @Override
    public SeckillSkuRedisTo getSeckillInfo(Long skuId) {
        BoundHashOperations<String, String, String> ops =
                stringRedisTemplate.boundHashOps(SeckillConstant.SECKILL_SESSIONSKUS_REDIS_PREFIX);
        for (String key : ops.keys()) {
            if (Pattern.matches("\\d-" + skuId, key)) {
                String v = (String) ops.get(key);
                SeckillSkuRedisTo redisTo = JSON.parseObject(v, SeckillSkuRedisTo.class);
                //当前商品参与秒杀活动
                if (redisTo != null) {
                    long current = System.currentTimeMillis();
                    //当前活动在有效期，暴露商品随机码返回
                    if (redisTo.getStartTime() < current && redisTo.getEndTime() > current) {
                        return redisTo;
                    }
                    redisTo.setRandomCode(null);
                    return redisTo;
                }
            }
        }
        return null;
    }

    @Override
    public String kill(String killId, String randomCode, Integer num) throws InterruptedException {
        String orderSn = null;
        BoundHashOperations<String, String, String> ops = stringRedisTemplate.boundHashOps(SeckillConstant.SECKILL_SESSIONSKUS_REDIS_PREFIX);
        ThreadLocal<MemberVo> threadLocal = LoginInterceptor.threadLocal;
        MemberVo memberVo = threadLocal.get();
        //秒杀商品是否存在
        String jsonString = ops.get(killId);
        if (jsonString != null) {
            SeckillSkuRedisTo seckillSkuRedisTo = JSON.parseObject(jsonString, SeckillSkuRedisTo.class);
            long cur = System.currentTimeMillis();
            //需要秒杀在规定时间内
            if (cur > seckillSkuRedisTo.getStartTime() && cur < seckillSkuRedisTo.getEndTime()) {
                //商品随机码匹配
                if (seckillSkuRedisTo.getRandomCode().equals(randomCode)) {
                    //购买数量要在秒杀范围内
                    if (num < seckillSkuRedisTo.getSeckillCount()) {
                        long ttl = seckillSkuRedisTo.getEndTime() - cur;
                        //判断是否购买过
                        String userByMark = SeckillConstant.SECKILL_USER_KILLED + memberVo.getId() + "-" + seckillSkuRedisTo.getSeckillCount();
                        if (stringRedisTemplate.opsForValue().setIfAbsent(userByMark, num.toString(), ttl, TimeUnit.MILLISECONDS)) {
                            String stockKey = SeckillConstant.SECKILL_STOCK_REDIS_PREFIX + seckillSkuRedisTo.getRandomCode();
                            RSemaphore semaphore = redissonClient.getSemaphore(stockKey);

                            boolean kiiSuccess = semaphore.tryAcquire(100, TimeUnit.MICROSECONDS);
                            if (kiiSuccess) {
                                //秒杀成功，给消息队列发送消息
                                orderSn = IdWorker.getTimeId();
                                SeckillOrderTo orderTo = new SeckillOrderTo();
                                orderTo.setMemberId(memberVo.getId());
                                orderTo.setNum(num);
                                orderTo.setOrderSn(orderSn);
                                orderTo.setPromotionSessionId(seckillSkuRedisTo.getPromotionSessionId());
                                orderTo.setSeckillPrice(seckillSkuRedisTo.getSeckillPrice());
                                orderTo.setSkuId(seckillSkuRedisTo.getSkuId());
                                //5.3 发送创建订单的消息
                                rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", orderTo);

                            }
                        }
                    }
                }
            }

        }
        return orderSn;
    }

    //缓存秒杀活动
    private void saveSessionInfos(List<SeckillSessionTo> seckillSessions) {
        for (SeckillSessionTo seckillSession : seckillSessions) {
            String key = SeckillConstant.SECKILL_SESSION_REDIS_PREFIX + seckillSession.getStartTime().getTime() + "_" + seckillSession.getEndTime().getTime();
            if (!stringRedisTemplate.hasKey(key)) {
                List<String> skus = seckillSession.getRelations().stream().map(item -> item.getPromotionSessionId() + "-" + item.getSkuId().toString()).collect(Collectors.toList());
                if (skus != null && skus.size() > 0) {
                    stringRedisTemplate.opsForList().leftPushAll(key, skus);
                }
            }

        }
    }

    private void saveSessionSkuInfos(List<SeckillSessionTo> seckillSessions) {
        //收集上架商品skuId，
        Set<Long> ids = new HashSet<>();
        for (SeckillSessionTo seckillSession : seckillSessions) {
            for (SeckillSkuTo relation : seckillSession.getRelations()) {
                ids.add(relation.getSkuId());
            }
        }
        //查出skuid 对应的具体信息返回map
        R r = productFeignService.getSkusToMap(ids);
        BoundHashOperations<String, Object, Object> ops = stringRedisTemplate.boundHashOps(SeckillConstant.SECKILL_SESSIONSKUS_REDIS_PREFIX);

        if (r.getCode() == 0) {
            Map<Long, SkuInfoVo> skuToMap = r.getData(new TypeReference<Map<Long, SkuInfoVo>>() {
            });
            for (SeckillSessionTo seckillSession : seckillSessions) {
                for (SeckillSkuTo sku : seckillSession.getRelations()) {
                    //秒杀场次+skuId 作为key，若存在，则该场次该商品已经上架
                    String key = sku.getPromotionSessionId().toString() + "-" + sku.getSkuId().toString();
                    if (!ops.hasKey(key)) {
                        SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();
                        BeanUtils.copyProperties(sku, redisTo);
                        redisTo.setSkuInfoVo(skuToMap.get(redisTo.getSkuId()));
                        redisTo.setStartTime(seckillSession.getStartTime().getTime());
                        redisTo.setEndTime(seckillSession.getEndTime().getTime());
                        //随机码
                        String token = UUID.randomUUID().toString().replace("-", "");
                        redisTo.setRandomCode(token);
                        //生成随机码防止恶意攻击
                        String json = JSON.toJSONString(redisTo);
                        ops.put(key, json);

                        //商品秒杀数量作为信号量
                        RSemaphore semaphore = redissonClient.getSemaphore(SeckillConstant.SECKILL_STOCK_REDIS_PREFIX + token);
                        semaphore.trySetPermits(redisTo.getSeckillCount());
                    }
                }
            }

        }


        if (r.getCode() == 0) {
        }
    }
}
