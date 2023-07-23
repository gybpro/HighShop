package com.high.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.high.shop.domain.User;
import com.high.shop.service.UserService;
import com.high.shop.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author high
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2023-07-23 12:53:47
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




