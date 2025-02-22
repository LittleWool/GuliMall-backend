package com.angenao.gulimallseckill.controller;

import com.angenao.common.to.SkuReductionTo;
import com.angenao.common.utils.R;
import com.angenao.gulimallseckill.service.SeckillSkuService;
import com.angenao.gulimallseckill.to.SeckillSkuRedisTo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName: SeckillController
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/20 17:49
 * @Version: 1.0
 **/
@Controller
public class SeckillController {


    @Autowired
    SeckillSkuService seckillSkuService;

    /**
     * 当前时间可以参与秒杀的商品信息
     *
     * @return
     */
    @GetMapping(value = "/getCurrentSeckillSkus")
    @ResponseBody
    public R getCurrentSeckillSkus() {
        //获取到当前可以参加秒杀商品的信息
        List<SeckillSkuRedisTo> vos = seckillSkuService.getCurrentSeckillSkus();

        return R.ok().setData(vos);
    }


    //根据skuId查询,该商品是否参与秒杀
    @PostMapping("/seckill/getSeckillInfo/{id}")
    @ResponseBody
    public R getSecKillInfo(@PathVariable(value = "id") Long skuId) {
        SeckillSkuRedisTo to = seckillSkuService.getSeckillInfo(skuId);
        return R.ok().setData(to);
    }

    //根据skuId查询,该商品是否参与秒杀
    @GetMapping("/kill")
    @ResponseBody
    public R kill(@Param("killId") String killId, @Param("key") String key, @Param("num") Integer num) throws InterruptedException {
        String orderSn = seckillSkuService.kill(killId, key, num);
        return R.ok().setData(orderSn);
    }

}
