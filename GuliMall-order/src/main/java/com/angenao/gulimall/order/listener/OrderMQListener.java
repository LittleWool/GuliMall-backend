package com.angenao.gulimall.order.listener;

import com.angenao.gulimall.order.entity.OrderEntity;
import com.angenao.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @ClassName: OrderMQListener
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/19 17:45
 * @Version: 1.0
 **/
@RabbitListener(queues = "order.release.order.queue")
@Slf4j
@Service
public class OrderMQListener {
    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void closeOrder(OrderEntity orderEntity, Channel channel, Message message) throws IOException {
       log.info("准备关闭超时订单");
        try {
            orderService.closeOrder(orderEntity);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            System.out.println(e.getMessage());
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);

        }
    }

}
