package com.high.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;

/**
 * 授权服务器配置类：(三方：授权服务器，资源服务器，客户端)
 *      1.配置第三方(客户端)账号信息
 *      2.配置token生成方式
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    // 用于密码授权的认证管理器
    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JwtAccessTokenConverter authorizationJwtAccessTokenConverter;

    private final TokenStore authorizationTokenStore;

    @Lazy
    public AuthorizationServerConfig(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtAccessTokenConverter authorizationJwtAccessTokenConverter, TokenStore authorizationTokenStore) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.authorizationJwtAccessTokenConverter = authorizationJwtAccessTokenConverter;
        this.authorizationTokenStore = authorizationTokenStore;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAccessTokenConverter authorizationJwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();

        // 加载私钥
        ClassPathResource resource = new ClassPathResource("rsa/sz2207.jks");

        // 通过密钥工厂生成密钥
        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(resource, "sz2207".toCharArray());

        KeyPair keyPair = factory.getKeyPair("sz2207");

        jwtAccessTokenConverter.setKeyPair(keyPair);

        return jwtAccessTokenConverter;
    }

    @Bean
    public TokenStore authorizationTokenStore() {
        return new JwtTokenStore(authorizationJwtAccessTokenConverter);
    }

    /*
    配置第三方(客户端)账号信息
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // web账号，用于后台管理系统的认证和授权
        clients.inMemory()
                .withClient("web")
                .secret(passwordEncoder.encode("web-secret"))
                .scopes("all")
                .authorizedGrantTypes("password")// 密码授权
                .accessTokenValiditySeconds(21600)// 6小时有效
                .redirectUris("https://www.baidu.com")
                .and()
                .withClient("app")
                .secret(passwordEncoder.encode("app-secret"))
                .scopes("all")
                .authorizedGrantTypes("client_credentials")// 客户端授权
                .accessTokenValiditySeconds(Integer.MAX_VALUE)// 永久有效
                .redirectUris("https://www.baidu.com");
    }

    /*
    配置token生成方式
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .tokenStore(authorizationTokenStore)
                .accessTokenConverter(authorizationJwtAccessTokenConverter);
    }
}
