package com.angenao.gulimall.order.dao;

import com.angenao.gulimall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单项信息
 * 
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 15:18:44
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {

}
