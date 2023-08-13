package com.high.shop.listener;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.high.shop.constant.QueueConstant;
import com.high.shop.domain.Order;
import com.high.shop.entity.ChangeStock;
import com.high.shop.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@Component
@Slf4j
public class OrderDeadListener {

    private final OrderService orderService;

    public OrderDeadListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = {QueueConstant.ORDER_DEAD_QUEUE})
    public void handlerOrderDeadMsg(Message message, Channel channel) {
        // 获取消息，构建json对象
        JSONObject jsonObject = JSONObject.parseObject(new String(message.getBody()));
        // 获取订单编号
        String orderNumber = jsonObject.getString("orderNumber");
        // 获取扣减商品库存数量对象
        ChangeStock changeStock = jsonObject.getObject("changeStock", ChangeStock.class);

        // 根据订单编号查询订单
        Order order = orderService.getOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderNumber, orderNumber)
        );

        // 判断订单是否存在
        if (ObjectUtils.isEmpty(order)) {
            log.error("订单编号无效{}", orderNumber);
            try {
                // 签收消息
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        // 判断订单是否已支付
        if (order.getIsPayed().equals(1)) {
            // 订单已支付
            try {
                // 签收消息
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        // 订单是否为未支付状态(1.确认超时未支付 2.支付状态未知，需要调用订单查询接口确定)
        // 1.确认超时未支付
        // 对订单进行回滚操作
        orderService.orderRollBack(order, changeStock);
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
