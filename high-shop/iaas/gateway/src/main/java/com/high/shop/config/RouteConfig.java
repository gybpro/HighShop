package com.high.shop.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.high.shop.constant.GatewayConstant;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

/**
 * 路由配置类
 *  拦截登录请求，访问授权服务器进行授权
 *  拦截此次请求的响应体数据(token令牌等数据)，存入Redis中
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class RouteConfig {
    private final StringRedisTemplate stringRedisTemplate;

    public RouteConfig(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 路由操作，拦截登录请求
                // path()，路径是否匹配
                .route(r -> r.path(GatewayConstant.WHITE_URL.get(0))
                        // 将响应体数据(token令牌等数据)存入Redis中
                        // filters()，过滤规则
                        // modifyResponseBody(inClass, outClass, rewriteFunction)，
                        // 修改响应体(输入类型，输出类型，重写/修改函数)
                        .filters(f -> f.modifyResponseBody(String.class, String.class, ((exchange, json) -> {
                            // 将返回的数据存入到Redis中
                            if (StringUtils.isBlank(json)) {
                                // 获取json中的token令牌及过期时间
                                JSONObject jsonObject = JSON.parseObject(json);

                                if (jsonObject.containsKey(GatewayConstant.ACCESS_TOKEN_PREFIX) &&
                                        jsonObject.containsKey(GatewayConstant.EXPIRES_IN_PREFIX)) {
                                    // 获取token和过期时间
                                    String accessToken = jsonObject.getString(GatewayConstant.ACCESS_TOKEN_PREFIX);
                                    long expiresIn = jsonObject.getLongValue(GatewayConstant.EXPIRES_IN_PREFIX);

                                    // 存入Redis中
                                    stringRedisTemplate.opsForValue().set(
                                            // key：用户还未登录，无法通过指定标识识别用户，用token识别
                                            GatewayConstant.JWT_TOKEN_PREFIX + accessToken,
                                            // value：用户还未登录，信息为空
                                            GatewayConstant.EMPTY_STRING,
                                            // 过期时间
                                            expiresIn,
                                            // 时间单位
                                            TimeUnit.SECONDS
                                    );
                                }
                            }
                            // 将数据响应到前端
                            return Mono.just(json);
                        })))
                        // 负载均衡访问授权服务器
                        .uri("lb://auth-server"))
                .build();
    }

}
