package com.high.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.high.shop.domain.Prod;
import com.high.shop.service.ProdService;
import com.high.shop.mapper.ProdMapper;
import org.springframework.stereotype.Service;

/**
* @author high
* @description 针对表【prod(商品)】的数据库操作Service实现
* @createDate 2023-07-02 16:12:42
*/
@Service
public class ProdServiceImpl extends ServiceImpl<ProdMapper, Prod>
    implements ProdService{

}




