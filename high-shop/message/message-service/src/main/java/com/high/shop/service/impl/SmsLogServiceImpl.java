package com.high.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.high.shop.domain.SmsLog;
import com.high.shop.service.SmsLogService;
import com.high.shop.mapper.SmsLogMapper;
import org.springframework.stereotype.Service;

/**
 * @author high
 * @description 针对表【sms_log(短信记录表)】的数据库操作Service实现
 * @createDate 2023-08-02 18:38:23
 */
@Service
public class SmsLogServiceImpl extends ServiceImpl<SmsLogMapper, SmsLog>
    implements SmsLogService{

}




