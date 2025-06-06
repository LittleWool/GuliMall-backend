package com.angenao.gulimall.ware.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: MyRabbitMQconfig
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/15 21:56
 * @Version: 1.0
 **/
@Configuration
@EnableRabbit
public class MyRabbitMQconfig {


//    @Autowired
//    RabbitTemplate rabbitTemplate;

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }





    @Bean
    public Exchange exchange() {
        return new TopicExchange("stock-event-exchange",true,false);

    }

    @Bean
    public Queue stockDelayqueue() {
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", "stock-event-exchange");
        arguments.put("x-dead-letter-routing-key", "stock.release");
        arguments.put("x-message-ttl", 120000);
        return new Queue("stock.delay.queue",true,false,false,arguments);
    }
    @Bean
    public Queue stockReleaseStockQueue() {
        return new Queue("stock.release.stock.queue",true,false,false);
    }

    @Bean
    public Binding stockLockedBinding() {
        return new Binding("stock.delay.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.locked",null);

    }

    @Bean   Binding stockReleaseBinding() {
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.release.#",null);
    }

}
