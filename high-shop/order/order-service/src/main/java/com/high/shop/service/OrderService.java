package com.high.shop.service;

import com.high.shop.domain.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.high.shop.entity.ChangeStock;
import com.high.shop.vo.OrderVO;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author high
 * @description 针对表【order(订单表)】的数据库操作Service
 * @createDate 2023-08-06 14:57:18
 */
public interface OrderService extends IService<Order> {

    /**
     * 提交订单
     * @param userId
     * @param orderVO
     * @return
     */
    String submitOrder(String userId, OrderVO orderVO);

    /**
     * 回滚订单
     * @param order
     * @param changeStock
     */
    @Transactional(rollbackFor = RuntimeException.class)
    void orderRollBack(Order order, ChangeStock changeStock);

}
