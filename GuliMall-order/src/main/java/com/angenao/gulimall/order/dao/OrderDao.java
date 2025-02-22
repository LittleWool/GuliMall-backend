package com.angenao.gulimall.order.dao;

import com.angenao.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 * 
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 15:18:44
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    void updateOrderStatus(@Param("orderSn") String outTradeNo,@Param("status") Integer code,@Param("payMethod") Integer alipay);
}
