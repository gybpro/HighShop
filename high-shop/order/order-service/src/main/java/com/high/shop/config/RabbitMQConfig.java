package com.high.shop.config;

import com.high.shop.constant.QueueConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class RabbitMQConfig {

    /**
     * 创建订单延迟队列
     * @return
     */
    @Bean
    public Queue orderMsQueue() {
        // 配置延迟队列
        Map<String, Object> map = new HashMap<>(8);
        // 配置消息存活时间
        map.put("x-message-ttl", 60 * 1000);
        // 配置死信队列
        map.put("x-dead-letter-exchange", QueueConstant.ORDER_DEAD_EXCHANGE);
        // 配置死信路由键
        map.put("x-dead-letter-routing-key", QueueConstant.ORDER_DEAD_ROUTING_KEY);

        return new Queue(QueueConstant.ORDER_MSG_QUEUE, true, false, false, map);
    }

    /**
     * 创建订单死信队列
     * @return
     */
    @Bean
    public Queue orderDeadQueue() {
        return new Queue(QueueConstant.ORDER_DEAD_QUEUE);
    }

    /**
     * 创建直连模式的死信队列交换机
     * @return
     */
    @Bean
    public DirectExchange orderDeadExchange() {
        return new DirectExchange(QueueConstant.ORDER_DEAD_EXCHANGE);
    }

    /**
     * 创建订单死信队列绑定关系
     * @return
     */
    @Bean
    public Binding orderDeadBinding() {
        return BindingBuilder.bind(orderDeadQueue()).to(orderDeadExchange()).with(QueueConstant.ORDER_DEAD_ROUTING_KEY);
    }

}
