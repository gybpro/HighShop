package com.high.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
// 开启配置属性
@EnableConfigurationProperties
// 开启Spring集成的缓存(Redis)
@EnableCaching
@EnableDiscoveryClient
@SpringBootApplication
public class ManagerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManagerServiceApplication.class, args);
    }
}
