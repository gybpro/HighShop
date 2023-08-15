package com.high.shop.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.high.shop.constant.QueueConstant;
import com.high.shop.domain.Order;
import com.high.shop.domain.OrderItem;
import com.high.shop.entity.ChangeStock;
import com.high.shop.entity.ProdChange;
import com.high.shop.entity.SkuChange;
import com.high.shop.entity.WxMsg;
import com.high.shop.feign.OrderCartFeign;
import com.high.shop.feign.OrderProductFeign;
import com.high.shop.properties.WxMsgProperties;
import com.high.shop.service.OrderItemService;
import com.high.shop.service.OrderService;
import com.high.shop.mapper.OrderMapper;
import com.high.shop.vo.OrderVO;
import com.high.shop.vo.ShopOrder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author high
 * @description 针对表【order(订单表)】的数据库操作Service实现
 * @createDate 2023-08-06 14:57:18
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
    implements OrderService{

    private final OrderItemService orderItemService;

    private final OrderCartFeign orderCartFeign;

    private final OrderProductFeign orderProductFeign;

    private final RabbitTemplate rabbitTemplate;

    private final Snowflake snowflake;

    private final WxMsgProperties wxMsgProperties;

    public OrderServiceImpl(OrderItemService orderItemService, OrderCartFeign orderCartFeign, OrderProductFeign orderProductFeign, RabbitTemplate rabbitTemplate, Snowflake snowflake, WxMsgProperties wxMsgProperties) {
        this.orderItemService = orderItemService;
        this.orderCartFeign = orderCartFeign;
        this.orderProductFeign = orderProductFeign;
        this.rabbitTemplate = rabbitTemplate;
        this.snowflake = snowflake;
        this.wxMsgProperties = wxMsgProperties;
    }

    /**
     * 用户提交订单：
     * 1.判断请求来源，是否清楚购物车
     * 2.修改商品prod和sku库存数量 -> 返回扣减商品库存数量对象
     * 3.生成订单（写数据到order和order_item表）
     * 4.写延迟队列（解决订单超时未支付问题）
     *
     * @param userId
     * @param orderVO
     * @return
     */
    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public String submitOrder(String userId, OrderVO orderVO) {
        // 1.判断请求来源，是否清楚购物车
        Integer source = orderVO.getSource();
        if (source == 1) {
            // 清除购物车
            clearUserCart(userId, orderVO);
        }

        // 2.修改商品prod和sku库存数量 -> 返回扣减商品库存数量对象
        ChangeStock changeStock = changeProdAndSkuStock(orderVO);

        // 3.生成订单（写数据到order和order_item表）
        // 使用雪花算法生成全局唯一的订单编号
        String orderNumber = generateOrderNumber();
        // 写订单
        String productName = writeOrder(userId, orderNumber, orderVO);
        // 发送消息到公众号
        sendWxMsg(userId, orderVO, productName);

        // 4.写延迟队列（解决订单超时未支付问题）
        sendOrderMsg(orderNumber, changeStock);

        return orderNumber;
    }

    private void clearUserCart(String userId, OrderVO orderVO) {
        List<Long> skuIdList = new ArrayList<>();

        // 获取所有的订单店铺对象集合
        List<ShopOrder> shopOrderList = orderVO.getShopCartOrders();
        shopOrderList.forEach(shopOrder -> {
            List<OrderItem> orderItemList = shopOrder.getShopCartItemDiscounts();
            // 遍历获取商品skuId
            orderItemList.forEach(orderItem -> skuIdList.add(orderItem.getSkuId()));
            // 远程接口：删除用户购买商品在购物车中的记录
            if (!orderCartFeign.deleteUserBasket(userId, skuIdList)) {
                throw new RuntimeException("用户提交订单：删除购物车中商品失败");
            }
        });
    }

    private ChangeStock changeProdAndSkuStock(OrderVO orderVO) {
        // 修改商品prod和sku库存数量 -> 返回扣减商品库存数量对象
        List<ProdChange> prodChangeList = new ArrayList<>();
        List<SkuChange> skuChangeList = new ArrayList<>();

        // 获取店铺订单信息
        List<ShopOrder> shopOrderList = orderVO.getShopCartOrders();
        // 遍历店铺订单信息
        shopOrderList.forEach(shopOrder -> {
            // 获取订单条目集合
            List<OrderItem> orderItemList = shopOrder.getShopCartItemDiscounts();

            // 遍历订单条目集合
            orderItemList.forEach(orderItem -> {
                // 获取修改库存信息
                Integer count = orderItem.getProdCount();
                Long prodId = orderItem.getProdId();
                Long skuId = orderItem.getSkuId();

                // 添加到sku修改列表
                skuChangeList.add(
                        SkuChange.builder()
                                .skuId(skuId)
                                .count(count)
                                .build()
                );

                // 添加到商品修改列表
                /* sku可以直接添加，prod不可以，prod有可能会重复
                prodChangeList.add(
                        ProdChange.builder()
                                .prodId(prodId)
                                .count(count)
                                .build()
                ); */
                // 查看prodChangeList中是否已有该商品
                List<ProdChange> prodChanges = prodChangeList.stream().filter(
                        prodChange -> prodChange.getProdId().equals(prodId)
                ).collect(Collectors.toList());
                // 判断是否已有该商品
                if (CollectionUtils.isEmpty(prodChanges)) {
                    // 没有该商品，则直接添加
                    prodChangeList.add(
                            ProdChange.builder()
                                    .prodId(prodId)
                                    .count(count)
                                    .build()
                    );
                } else {
                    // 已有该商品，则修改数量
                    ProdChange prodChange = prodChanges.get(0);
                    prodChange.setCount(prodChange.getCount() + count);
                }
            });
        });

        ChangeStock changeStock = ChangeStock.builder()
                .prodChangeList(prodChangeList)
                .skuChangeList(skuChangeList)
                .build();

        // 扣除MySQL中的库存信息
        orderProductFeign.updateProdAndSkuStock(changeStock);

        // 扣除ES中的库存信息(通过快速导入更新ES数据)
        rabbitTemplate.convertAndSend(
                QueueConstant.ES_CHANGE_EXCHANGE, QueueConstant.ES_CHANGE_ROUTING_KEY, JSON.toJSONString(prodChangeList)
        );

        return changeStock;
    }

    private String generateOrderNumber() {
        return snowflake.nextIdStr();
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public String writeOrder(String userId, String orderNumber, OrderVO orderVO) {
        // 获取店铺订单列表
        List<ShopOrder> shopOrderList = orderVO.getShopCartOrders();
        // 所有商品名称集合
        StringBuilder productNameBuilder = new StringBuilder();
        // 所有优惠金额集合
        List<BigDecimal> reduceList = new ArrayList<>();
        // 所有订单商品条目集合
        List<OrderItem> allOrderItems = new ArrayList<>();

        // 遍历店铺订单列表，获取店铺id集合、商品名称集合和优惠金额集合
        shopOrderList.forEach(shopOrder -> {
            // 获取店铺的商品条目集合
            List<OrderItem> orderItemList = shopOrder.getShopCartItemDiscounts();

            // 遍历商品条目集合，获取商品名称
            orderItemList.forEach(orderItem -> {
                orderItem.setOrderNumber(orderNumber);
                productNameBuilder.append(orderItem.getProdName()).append(",");
                allOrderItems.add(orderItem);
            });

            // 获取优惠金额
            reduceList.add(shopOrder.getShopReduce());
        });

        productNameBuilder.deleteCharAt(productNameBuilder.length() - 1);
        String productName = productNameBuilder.toString();

        // 批量添加订单商品条目
        orderItemService.saveBatch(allOrderItems);

        // 生成订单
        Order order = Order.builder()
                .prodName(productName)
                .userId(userId)
                .orderNumber(orderNumber)
                .total(orderVO.getTotal())
                .actualTotal(orderVO.getActualTotal())
                .payType(1)
                .remarks(orderVO.getRemarks())
                .orderStatus(1)
                .freightAmount(orderVO.getTransfee())
                .addrOrderId(orderVO.getUserAddr().getAddrId())
                .productNums(orderVO.getTotalCount())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .isPayed(0)
                .status(1)
                .refundSts(0)
                .reduceAmount(
                        reduceList.stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO)
                )
                .orderType(1)
                .build();

        // 保存订单
        save(order);

        return productName;
    }

    private void sendWxMsg(String userId, OrderVO orderVO, String productName) {
        Map<String, Map<String, String>> data = new HashMap<>(16);
        data.put("time", WxMsg.buildData(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        ), "#173177"));
        data.put("goods", WxMsg.buildData(productName, "#173177"));
        data.put("price", WxMsg.buildData(orderVO.getActualTotal().toPlainString(), "#173177"));
        data.put("money", WxMsg.buildData(new BigDecimal(999999999).toPlainString(), "#173177"));

        WxMsg wxMsg = WxMsg.builder()
                .toUser(userId)
                .topColor("#FF0000")
                .templateId(wxMsgProperties.getTemplateId())
                .data(data)
                .build();

        //发送消息到消息队列中，由message微服务，来发送微信公众号消息
        rabbitTemplate.convertAndSend(
                QueueConstant.WX_MSG_QUEUE,
                JSON.toJSONString(wxMsg)
        );
    }

    private void sendOrderMsg(String orderNumber, ChangeStock changeStock) {
        // 将数据转换为json格式
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderNumber", orderNumber);
        jsonObject.put("changeStock", changeStock);

        // 发送延迟消息
        rabbitTemplate.convertAndSend(
                QueueConstant.ORDER_MSG_QUEUE,
                jsonObject.toJSONString()
        );
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void orderRollBack(Order order, ChangeStock changeStock) {
        // 回滚库存数量
        List<ProdChange> prodChangeList = changeStock.getProdChangeList();
        List<SkuChange> skuChangeList = changeStock.getSkuChangeList();
        prodChangeList.forEach(prodChange -> prodChange.setCount(prodChange.getCount() * -1));
        skuChangeList.forEach(skuChange -> skuChange.setCount(skuChange.getCount() * -1));
        orderProductFeign.updateProdAndSkuStock(changeStock);

        // 更新订单状态
        order.setOrderStatus(6)
                .setCloseType(1)
                .setUpdateTime(LocalDateTime.now())
                .setCancelTime(LocalDateTime.now())
                .setFinallyTime(LocalDateTime.now());
        updateById(order);
    }

    @Override
    public boolean changeOrderStatus(String orderNumber) {
        // 根据订单编号查询订单信息
        Order order = getOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderNumber, orderNumber)
        );

        if (ObjectUtils.isEmpty(order)) {
            throw new RuntimeException("订单查询异常");
        }

        // 修改订单状态
        order.setOrderStatus(2)
                .setIsPayed(1)
                .setPayType(2)
                .setPayTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());

        return updateById(order);
    }

}




