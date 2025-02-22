package com.angenao.gulimall.ware.Listerer;

import com.angenao.common.mq.OrderTo;
import com.angenao.common.mq.StockLockedTo;
import com.angenao.gulimall.ware.service.WareSkuService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.io.IOException;


/**
 * @ClassName: WareStockListener
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/19 15:07
 * @Version: 1.0
 **/

@RabbitListener(queues = "stock.release.stock.queue")
@Service
@Slf4j
public class WareStockListener {

    @Autowired
    WareSkuService wareSkuService;

    @RabbitHandler
    public void unLockStock(StockLockedTo stockLockedTo, Channel channel, Message message) throws IOException {
        log.info("收到过期引起的库存解锁信息");
        try{
            wareSkuService.unLock(stockLockedTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e){
            System.out.println(e.getMessage());
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

    }

    @RabbitHandler
    public void orderCloseUnlockStock(OrderTo order, Channel channel, Message message) throws IOException {
        log.info("收到由订单取消引发的库存解锁信息");
        try{
            wareSkuService.unLock(order);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e){
            System.out.println(e.getMessage());
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

}
