package com.high.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.high.shop.domain.Basket;
import com.high.shop.service.BasketService;
import com.high.shop.mapper.BasketMapper;
import org.springframework.stereotype.Service;

/**
 * @author high
 * @description 针对表【basket(购物车)】的数据库操作Service实现
 * @createDate 2023-08-05 09:16:09
 */
@Service
public class BasketServiceImpl extends ServiceImpl<BasketMapper, Basket>
    implements BasketService{

}




