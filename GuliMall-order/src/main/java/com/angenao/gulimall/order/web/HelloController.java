package com.angenao.gulimall.order.web;

import com.angenao.gulimall.order.entity.OrderEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.UUID;

/**
 * @ClassName: HelloController
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/16 11:07
 * @Version: 1.0
 **/

@Controller
public class HelloController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @RequestMapping("{page}.html")
    public String index(@PathVariable("page") String page) {
        return page;
    }
    @GetMapping("/test/createOrder")
    public String createOrder() {
       OrderEntity orderEntity = new OrderEntity();
       orderEntity.setOrderSn(UUID.randomUUID().toString());
       orderEntity.setModifyTime(new Date());
        rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", orderEntity);
        return "ok";
    }
}
