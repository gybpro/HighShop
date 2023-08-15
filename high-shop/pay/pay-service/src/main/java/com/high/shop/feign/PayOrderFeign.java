package com.high.shop.feign;

import com.high.shop.domain.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@FeignClient("order-service")
public interface PayOrderFeign {

    /**
     * 根据订单编号查询订单信息
     * @param orderNumber
     * @return
     */
    @GetMapping("/p/myOrder/getOrderByOrderNumber")
    Order getOrderByOrderNumber(@RequestParam("orderNumber") String orderNumber);

    /**
     * 修改订单状态
     * @param orderNumber
     * @return
     */
    @GetMapping("/p/myOrder/changeOrderStatus")
    boolean changeOrderStatus(@RequestParam("orderNumber") String orderNumber);
}
