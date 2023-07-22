package com.high.shop.config;

import com.high.shop.constant.QueueConstant;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class RabbitMQConfig {

    // 如果不声明交换机和routingKey，那么默认就是direct类型的
    @Bean
    public Queue esChangeQueue() {
        return new Queue(QueueConstant.ES_CHANGE_QUEUE);
    }

    @Bean
    public Exchange esChangeExchange() {
        return new DirectExchange(QueueConstant.ES_CHANGE_EXCHANGE);
    }

    @Bean
    public Binding esChangeBinding() {
        return BindingBuilder.bind(esChangeQueue()).to(esChangeExchange()).with(QueueConstant.ES_CHANGE_ROUTING_KEY).noargs();
    }

}
