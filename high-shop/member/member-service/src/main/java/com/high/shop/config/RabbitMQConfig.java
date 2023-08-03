package com.high.shop.config;

import com.high.shop.constant.QueueConstant;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue smsQueue() {
        return new Queue(QueueConstant.PHONE_SMS_QUEUE);
    }

    @Bean
    public Queue wxQueue() {
        return new Queue(QueueConstant.WX_MSG_QUEUE);
    }
}
