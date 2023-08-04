package com.high.shop.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.high.shop.constant.QueueConstant;
import com.high.shop.entity.WxMsg;
import com.high.shop.properties.WxMsgProperties;
import com.high.shop.util.SmsUtils;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.client.RestTemplate;

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

    private final WxMsgProperties wxMsgProperties;

    private final RestTemplate restTemplate;

    private final StringRedisTemplate redisTemplate;

    public RabbitMQListener(SmsUtils smsUtils, WxMsgProperties wxMsgProperties, RestTemplate restTemplate, StringRedisTemplate redisTemplate) {
        this.smsUtils = smsUtils;
        this.wxMsgProperties = wxMsgProperties;
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
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

    @RabbitListener(queues = {QueueConstant.WX_MSG_QUEUE})
    public void receiveWxMsg(Message message, Channel channel) {
        // 发送微信公众号模板消息
        String wxMsgJsonStr = new String(message.getBody());

        // 解析json数据
        WxMsg wxMsg = JSON.parseObject(wxMsgJsonStr, WxMsg.class);

        // 获取redis中的access_token
        String accessToken = redisTemplate.opsForValue().get("wx:msg:token");

        if (StringUtils.isEmpty(accessToken)) {
            return;
        }

        // 发送模板消息
        String resultJsonStr = restTemplate.postForObject(
                // url
                String.format(wxMsgProperties.getSendMsgUrl(), accessToken),
                // 请求参数
                wxMsg,
                // 响应类型
                String.class
        );

        JSONObject jsonObj = JSON.parseObject(resultJsonStr);

        if (jsonObj.containsKey("errcode")) {
            int errcode = jsonObj.getIntValue("errcode");

            if (errcode == 0) {
                // 消息发送成功，消费消息，记录消息日志
                try {
                    channel.basicAck(
                            message.getMessageProperties().getDeliveryTag(),
                            false
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
