package com.high.shop.constant;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
public interface QueueConstant {

    /**
     * es快速导入的监听队列
     */
    String ES_CHANGE_QUEUE = "es.change.queue";
    String ES_CHANGE_EXCHANGE = "es.change.exchange";
    String ES_CHANGE_ROUTING_KEY = "es.change";

    // 手机短信队列
    String PHONE_SMS_QUEUE = "phone.sms.queue";

    // 微信消息队列
    String WX_MSG_QUEUE = "wx.msg.queue";

}
