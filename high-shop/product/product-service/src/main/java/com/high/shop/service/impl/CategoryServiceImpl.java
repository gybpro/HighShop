package com.high.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.high.shop.domain.Category;
import com.high.shop.service.CategoryService;
import com.high.shop.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

/**
* @author high
* @description 针对表【category(产品类目)】的数据库操作Service实现
* @createDate 2023-07-02 16:12:42
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

}




