package com.high.shop.config;

import cn.hutool.core.io.FileUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.nio.charset.Charset;

/**
 * 资源服务器配置类
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
// 开启资源服务器
@EnableResourceServer
// 开启方法级的权限校验
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    private final JwtAccessTokenConverter resourceJwtAccessTokenConverter;

    private final TokenStore resourceTokenStore;

    @Lazy
    public ResourceServerConfig(JwtAccessTokenConverter resourceJwtAccessTokenConverter, TokenStore resourceTokenStore) {
        this.resourceJwtAccessTokenConverter = resourceJwtAccessTokenConverter;
        this.resourceTokenStore = resourceTokenStore;
    }

    @Bean
    public JwtAccessTokenConverter resourceJwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();

        String publicKey = FileUtil.readString("rsa/sz2207.txt", Charset.defaultCharset());

        jwtAccessTokenConverter.setVerifierKey(publicKey);

        return jwtAccessTokenConverter;
    }

    @Bean
    public TokenStore resourceTokenStore() {
        return new JwtTokenStore(resourceJwtAccessTokenConverter);
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenStore(resourceTokenStore);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors().disable();
        http.sessionManagement().disable();

        http.authorizeRequests()
                //放行不需要权限校验的路径
                // swagger druid actuator...
                .antMatchers("/v2/api-docs",
                        "/v3/api-docs",
                        //用来获取支持的动作
                        "/swagger-resources/configuration/ui",
                        //用来获取api-docs的URI
                        "/swagger-resources",
                        //安全选项
                        "/swagger-resources/configuration/security",
                        "/webjars/**",
                        "/swagger-ui/**",
                        "/druid/**",
                        "/actuator/**",
                        //发送微信公众号的模板消息，测试暂时放行
                        "/p/sms/sendWx",
                        //支付宝支付的回调通知地址，必须放行
                        "/p/order/alipayNotify"
                )
                .permitAll()
                .anyRequest()
                .authenticated();
    }
}
