package com.high.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.high.shop.domain.Notice;
import com.high.shop.service.NoticeService;
import com.high.shop.mapper.NoticeMapper;
import org.springframework.stereotype.Service;

/**
* @author high
* @description 针对表【notice】的数据库操作Service实现
* @createDate 2023-07-23 09:37:04
*/
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice>
    implements NoticeService{

}




