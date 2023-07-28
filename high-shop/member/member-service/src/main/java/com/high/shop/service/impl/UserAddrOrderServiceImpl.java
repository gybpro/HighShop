package com.high.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.high.shop.domain.UserAddrOrder;
import com.high.shop.service.UserAddrOrderService;
import com.high.shop.mapper.UserAddrOrderMapper;
import org.springframework.stereotype.Service;

/**
* @author high
* @description 针对表【user_addr_order(用户订单配送地址)】的数据库操作Service实现
* @createDate 2023-07-23 15:39:58
*/
@Service
public class UserAddrOrderServiceImpl extends ServiceImpl<UserAddrOrderMapper, UserAddrOrder>
    implements UserAddrOrderService{

}




