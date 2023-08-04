package com.high.shop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * 资源服务器配置类：
 *      1.设置资源的拦截和放行
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    // 设置资源的拦截和放行
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors().disable();
        http.sessionManagement().disable();

        // 请求授权访问操作
        http.authorizeRequests()
                // 健康检查的请求
                .antMatchers(
                        "/v2/api-docs",
                        "/v3/api-docs",
                        // 用来获取支持的动作
                        "/swagger-resources/configuration/ui",
                        // 用来获取api-docs的URI
                        "/swagger-resources",
                        // 安全选项
                        "/swagger-resources/configuration/security",
                        "/webjars/**",
                        "/swagger-ui/**",
                        "/druid/**",
                        "/actuator/**",
                        // 发送微信公众号消息，测试暂时放行
                        "/p/sms/sendWxMsg"
                )
                // 放行以上所有请求
                .permitAll()
                // 其他请求
                .anyRequest()
                // 必须认证后才能访问
                .authenticated();
    }
}
