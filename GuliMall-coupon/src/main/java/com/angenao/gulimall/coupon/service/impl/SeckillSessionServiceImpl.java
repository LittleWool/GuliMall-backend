package com.angenao.gulimall.coupon.service.impl;

import com.angenao.gulimall.coupon.entity.SeckillSkuRelationEntity;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.Query;

import com.angenao.gulimall.coupon.dao.SeckillSessionDao;
import com.angenao.gulimall.coupon.entity.SeckillSessionEntity;
import com.angenao.gulimall.coupon.service.SeckillSessionService;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    private final SeckillSkuRelationServiceImpl seckillSkuRelationService;

    public SeckillSessionServiceImpl(SeckillSkuRelationServiceImpl seckillSkuRelationService) {
        this.seckillSkuRelationService = seckillSkuRelationService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SeckillSessionEntity> getSeckillSessionsIn3Days() {
        String startTime = getStartTime();
        String endTime = getEndTime();
        List<SeckillSessionEntity> sessions = list(new QueryWrapper<SeckillSessionEntity>().between("start_time", startTime, endTime));
        List<Long> sessionIds = sessions.stream().map(SeckillSessionEntity::getId).collect(Collectors.toList());
       // baseMapper.getSeckillSessionRelationEntitiesByIds(sessionIds);
        //查询每个session相关的秒杀商品
        sessions.forEach(session -> {
            List<SeckillSkuRelationEntity> relationEntities = seckillSkuRelationService.list(new QueryWrapper<SeckillSkuRelationEntity>().eq("promotion_session_id", session.getId()));
            session.setRelations(relationEntities);}
        );
        return sessions;
    }

    private String getStartTime(){
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.MIN;
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(formatter);
    }
    private String getEndTime(){
        LocalDate localDate = LocalDate.now().plusDays(2);
        LocalTime localTime = LocalTime.MAX;
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(formatter);
    }
}