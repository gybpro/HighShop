package com.high.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.high.shop.domain.Sku;
import com.high.shop.service.SkuService;
import com.high.shop.mapper.SkuMapper;
import org.springframework.stereotype.Service;

/**
* @author high
* @description 针对表【sku(单品SKU表)】的数据库操作Service实现
* @createDate 2023-07-02 16:12:42
*/
@Service
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku>
    implements SkuService{

}




