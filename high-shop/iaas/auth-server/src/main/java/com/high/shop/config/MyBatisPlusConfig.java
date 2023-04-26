package com.high.shop.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@MapperScan("com.high.shop.mapper")
@Configuration
public class MyBatisPlusConfig {
}
