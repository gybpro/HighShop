package com.high.shop.config;

import cn.hutool.core.lang.Snowflake;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class SnowflakeConfig {

    @Bean
    public Snowflake snowflake() {
        return new Snowflake(0, 0);
    }

}
