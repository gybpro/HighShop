package com.high.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.high.shop.base.BaseOrderController;
import com.high.shop.constant.OrderConstant;
import com.high.shop.domain.*;
import com.high.shop.feign.OrderCartFeign;
import com.high.shop.feign.OrderMemberFeign;
import com.high.shop.feign.OrderProductFeign;
import com.high.shop.service.OrderService;
import com.high.shop.vo.OrderConfirm;
import com.high.shop.vo.OrderStatusCount;
import com.high.shop.vo.OrderVO;
import com.high.shop.vo.ShopOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/p/myOrder")
public class OrderController extends BaseOrderController {

    private final OrderService orderService;

    private final OrderMemberFeign orderMemberFeign;

    private final OrderProductFeign orderProductFeign;

    private final OrderCartFeign orderCartFeign;

    public OrderController(OrderService orderService, OrderMemberFeign orderMemberFeign, OrderProductFeign orderProductFeign, OrderCartFeign orderCartFeign) {
        this.orderService = orderService;
        this.orderMemberFeign = orderMemberFeign;
        this.orderProductFeign = orderProductFeign;
        this.orderCartFeign = orderCartFeign;
    }

    @GetMapping("/orderCount")
    public ResponseEntity<OrderStatusCount> orderCount() {
        // 待支付状态数量
        int unPayCount = orderService.count(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getUserId, getAuthenticationUserId())
                        .eq(Order::getOrderStatus, OrderConstant.ORDER_STATUS_PAY)
        );

        // 待发货状态数量
        int payedCount = orderService.count(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getUserId, getAuthenticationUserId())
                        .eq(Order::getOrderStatus, OrderConstant.ORDER_STATUS_DELIVER)
        );

        // 待收货状态数量
        int consignmentCount = orderService.count(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getUserId, getAuthenticationUserId())
                        .eq(Order::getOrderStatus, OrderConstant.ORDER_STATUS_RECEIVE)
        );

        return ok(
                OrderStatusCount.builder()
                        .unPay(unPayCount)
                        .payed(payedCount)
                        .consignment(consignmentCount)
                        .build()
        );
    }

    @PostMapping("/confirm")
    public ResponseEntity<OrderVO> confirm(@RequestBody OrderConfirm confirm) {
        // 创建OrderVO对象
        OrderVO orderVO = new OrderVO();

        // 查询用户默认收货地址
        UserAddr userAddr = orderMemberFeign.defaultAddr(getAuthenticationUserId());

        // 设置订单收货地址
        orderVO.setUserAddr(
                ObjectUtils.isEmpty(userAddr) ? new UserAddr() : userAddr
        );

        // 获取购物车信息，判断用户入口
        List<Long> basketIds = confirm.getBasketIds();

        if (CollectionUtils.isEmpty(basketIds)) {
            // 无购物车信息，则从商品详情页面进入
            prodToConfirm(orderVO, confirm.getOrderItem());
        } else {
            // 有购物车信息，则从购物车页面进入
            basketToConfirm(orderVO, basketIds);
        }

        return ok(orderVO);
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submit(@RequestBody OrderVO orderVO) {
        String userId = getAuthenticationUserId();
        String orderNumber = orderService.submitOrder(userId, orderVO);
        return ok(orderNumber);
    }

    private void prodToConfirm(OrderVO orderVO, OrderItem orderItem) {
        // 根据skuId获取sku信息，补充orderItem信息
        List<Sku> skuList = orderProductFeign.getSkuListByIds(Collections.singletonList(orderItem.getSkuId()));

        if (CollectionUtils.isEmpty(skuList)) {
            throw new RuntimeException("服务器开小差了");
        }

        Sku sku = skuList.get(0);

        // 计算总金额
        BigDecimal totalMoney = sku.getPrice().multiply(new BigDecimal(orderItem.getProdCount()));

        // 将sku对象的属性值copy到orderItem中
        BeanUtils.copyProperties(sku, orderItem);

        // 补充orderItem信息
        orderItem.setUserId(getAuthenticationUserId())
                .setProductTotalAmount(totalMoney)
                .setCommSts(0)
                .setRecTime(LocalDateTime.now())
                .setBasketDate(LocalDateTime.now());

        // 计算应付运费，如果小于99，则需要运费，否则包邮
        BigDecimal freight = totalMoney.compareTo(
                new BigDecimal(99)
        ) < 0 ? new BigDecimal(10) : BigDecimal.ZERO;

        // 补充orderVO信息
        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemList.add(orderItem);

        ShopOrder shopOrder = ShopOrder.builder()
                .shopCartItemDiscounts(orderItemList)
                .build();
        List<ShopOrder> shopOrderList = new ArrayList<>();
        shopOrderList.add(shopOrder);

        orderVO.setShopCartOrders(shopOrderList)
                .setTotalCount(orderItem.getProdCount())
                .setTotal(totalMoney)
                .setTransfee(freight)
                .setActualTotal(totalMoney.add(freight))
                .setSource(0);
    }

    private void basketToConfirm(OrderVO orderVO, List<Long> basketIds) {
        // 获取购物车信息
        List<Basket> basketList = orderCartFeign.getBasketListByIds(basketIds);

        if (CollectionUtils.isEmpty(basketList)) {
            throw new RuntimeException("服务器开小差了");
        }

        // 根据skuIds获取skuList信息，补充orderItem信息
        List<Long> skuIds = basketList.stream().map(Basket::getSkuId).collect(Collectors.toList());
        List<Sku> skuList = orderProductFeign.getSkuListByIds(skuIds);

        if (CollectionUtils.isEmpty(skuList)) {
            throw new RuntimeException("服务器开小差了");
        }

        // 根据shopId分组
        Map<Long, List<Basket>> shopOrderMap = basketList.stream().collect(Collectors.groupingBy(Basket::getShopId));

        List<ShopOrder> shopOrderList = new ArrayList<>();
        List<BigDecimal> shopMoneyList = new ArrayList<>();
        List<BigDecimal> shopFreightList = new ArrayList<>();
        List<Integer> itemCountList = new ArrayList<>();

        shopOrderMap.forEach((shopId, baskets) -> {
            // 订单单店商品集合
            List<OrderItem> orderItemList = new ArrayList<>();
            // 单品总价集合
            List<BigDecimal> itemMoneyList = new ArrayList<>();

            baskets.forEach(basket -> {
                // 获取产品数量
                Integer prodCount = basket.getBasketCount();
                itemCountList.add(prodCount);

                // 根据skuId获取sku信息，补充orderItem信息
                Sku sku = getSku(skuList, basket);

                // 构建OrderItem
                OrderItem orderItem = OrderItem.builder()
                        .shopId(shopId)
                        .userId(getAuthenticationUserId())
                        .prodCount(prodCount)
                        .recTime(LocalDateTime.now())
                        .basketDate(basket.getBasketDate())
                        .commSts(0)
                        .build();

                // copy sku属性值到orderItem中
                BeanUtils.copyProperties(sku, orderItem);

                orderItemList.add(orderItem);

                // 计算商品总金额，并添加到itemMoneyList中
                itemMoneyList.add(
                        sku.getPrice().multiply(new BigDecimal(prodCount))
                );
            });
            // 计算店铺总金额，运费金额，并存入shopMoneyList和shopFreightList
            calcMoneyList(shopMoneyList, shopFreightList, itemMoneyList);

            BigDecimal freight = shopFreightList.get(shopFreightList.size() - 1);

            // 构建ShopOrder
            ShopOrder shopOrder = ShopOrder.builder()
                    .shopCartItemDiscounts(orderItemList)
                    .transfee(freight)
                    .build();

            shopOrderList.add(shopOrder);
        });

        Integer totalCount = itemCountList.stream().reduce(Integer::sum).orElse(0);

        // 计算订单总金额、最终金额、优惠金额(0)
        BigDecimal totalMoney = shopMoneyList.stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        BigDecimal freight = shopFreightList.stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

        orderVO.setShopCartOrders(shopOrderList)
                .setTotalCount(totalCount)
                .setTotal(totalMoney)
                .setTransfee(freight)
                .setActualTotal(totalMoney.add(freight))
                .setSource(1);
    }

    private static Sku getSku(List<Sku> skuList, Basket basket) {
        return skuList.stream().filter(
                sku -> sku.getSkuId().equals(basket.getSkuId())
        ).collect(Collectors.toList()).get(0);
    }

    private static void calcMoneyList(List<BigDecimal> shopMoneyList, List<BigDecimal> shopFreightList, List<BigDecimal> itemMoneyList) {
        // 计算店铺总金额，并添加到shopMoneyList
        BigDecimal shopTotalMoney = itemMoneyList.stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        shopMoneyList.add(shopTotalMoney);

        // 计算应付运费，如果小于99，则需要运费，否则包邮
        BigDecimal freight = shopTotalMoney.compareTo(
                new BigDecimal(99)
        ) < 0 ? new BigDecimal(10) : BigDecimal.ZERO;
        // 将运费添加到shopFreightList
        shopFreightList.add(freight);
    }

}
