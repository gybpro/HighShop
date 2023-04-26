package com.high.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
// 开启WebSecurity认证服务
@EnableWebSecurity
// 开启Oauth2授权服务器
@EnableAuthorizationServer
// 开启Oauth2资源服务器
@EnableResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class AuthServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }
}
