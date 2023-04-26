package com.high.shop.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 网关常量类
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public interface GatewayConstant {
    // 空字符串
    String EMPTY_STRING = "";

    // URL白名单
    // 放行登录操作，获取token的url地址
    List<String> WHITE_URL = Arrays.asList("/oauth/token", "https://codegeex.cn");

    // 客户端传来的token前缀
    String AUTHORIZATION_PREFIX = "Authorization";

    // bearer前缀
    String BEARER_PREFIX = "Bearer ";

    // 授权服务器响应数据中的token前缀
    String ACCESS_TOKEN_PREFIX = "access_token";
    // 响应数据中的token过期时间前缀
    String EXPIRES_IN_PREFIX = "expires_in";

    // 存入Redis的token前缀
    String JWT_TOKEN_PREFIX = "oauth:jwt:";
}
