package com.angenao.gulimall.coupon.dao;

import com.angenao.gulimall.coupon.entity.SeckillSessionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 秒杀活动场次
 * 
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 14:43:14
 */
@Mapper
public interface SeckillSessionDao extends BaseMapper<SeckillSessionEntity> {

    void getSeckillSessionsIn3Days(@Param("startTime") String startTime,@Param("endTime") String endTime);
}
