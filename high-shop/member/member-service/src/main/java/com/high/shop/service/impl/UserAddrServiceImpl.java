package com.high.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.high.shop.domain.UserAddr;
import com.high.shop.service.UserAddrService;
import com.high.shop.mapper.UserAddrMapper;
import org.springframework.stereotype.Service;

/**
* @author high
* @description 针对表【user_addr(用户配送地址)】的数据库操作Service实现
* @createDate 2023-07-23 15:39:58
*/
@Service
public class UserAddrServiceImpl extends ServiceImpl<UserAddrMapper, UserAddr>
    implements UserAddrService{

}




