package com.high.shop.config;

import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeWithHBServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class AliPayConfig {

    public AliPayConfig() {
        Configs.init("alipay/zfbinfo.properties");
    }

    @Bean
    public AlipayTradeService alipayTradeService() {
        return new AlipayTradeWithHBServiceImpl.ClientBuilder().build();
    }

}
