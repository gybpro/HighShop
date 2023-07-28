package com.high.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.high.shop.domain.UserCollection;
import com.high.shop.service.UserCollectionService;
import com.high.shop.mapper.UserCollectionMapper;
import org.springframework.stereotype.Service;

/**
* @author high
* @description 针对表【user_collection】的数据库操作Service实现
* @createDate 2023-07-23 15:39:58
*/
@Service
public class UserCollectionServiceImpl extends ServiceImpl<UserCollectionMapper, UserCollection>
    implements UserCollectionService{

}




