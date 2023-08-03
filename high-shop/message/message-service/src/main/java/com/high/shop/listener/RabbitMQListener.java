package com.high.shop.listener;

import com.alibaba.fastjson.JSON;
import com.high.shop.constant.QueueConstant;
import com.high.shop.util.SmsUtils;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Map;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class RabbitMQListener {

    private final SmsUtils smsUtils;

    public RabbitMQListener(SmsUtils smsUtils) {
        this.smsUtils = smsUtils;
    }

    @RabbitListener(queues = {QueueConstant.PHONE_SMS_QUEUE})
    public void receiveSmsMessage(Message message, Channel channel) {
        // 接收消息，调用短信服务
        String jsonMsg = new String(message.getBody());

        Map<String, String> msg = JSON.parseObject(jsonMsg, Map.class);

        // boolean flag = smsUtils.sendCode(msg.get("phoneNum"),  msg.get("code"));

        boolean flag = true;

        if (flag) {
            try {
                channel.basicAck(
                        // deliveryTag是消息的交货标签/id，用于确认消费该消息
                        message.getMessageProperties().getDeliveryTag(),
                        false
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
