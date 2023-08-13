package com.high.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.high.shop.domain.OrderItem;
import com.high.shop.service.OrderItemService;
import com.high.shop.mapper.OrderItemMapper;
import org.springframework.stereotype.Service;

/**
 * @author high
 * @description 针对表【order_item(订单项)】的数据库操作Service实现
 * @createDate 2023-08-06 14:57:18
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem>
    implements OrderItemService{

}




