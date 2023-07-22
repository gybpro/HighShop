package com.high.shop.service;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
public interface ImportService {

    /**
     * 全量导入
     */
    void importAll();

    /**
     * 增量导入
     */
    void updateImport();

    /**
     * 快速导入(MQ)
     *
     * @param message 消息
     * @param channel 管道
     */
    void quickImport(Message message, Channel channel);

}
