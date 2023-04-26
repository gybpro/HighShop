package com.high.shop.filter;

import com.alibaba.fastjson.JSON;
import com.high.shop.constant.GatewayConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 网关过滤器
 *      1.放行登录等操作，交给auth-server去进行授权，返回token
 *      2.其他请求必须携带token，否则返回无权限的错误信息
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Configuration
public class JwtCheckFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求对象
        ServerHttpRequest request = exchange.getRequest();
        // 获取请求路径
        String path = request.getURI().getPath();

        log.info("Path : {}", path);

        // 判断是否为白名单URL
        if (GatewayConstant.WHITE_URL.contains(path)) {
            // 如果是白名单URL，则放行
            return chain.filter(exchange);
        }

        // 非白名单URL，则需要携带token进行验证
        // 请求头：Authorization = Bearer JwtToken
        // 获取请求头信息
        HttpHeaders headers = request.getHeaders();

        // 获取Authorization授权的token信息
        String authorization = headers.getFirst(GatewayConstant.AUTHORIZATION_PREFIX);
        if (authorization != null) {
            String token = authorization.replaceAll(GatewayConstant.BEARER_PREFIX, GatewayConstant.EMPTY_STRING);

            // 判断是否有token
            if (StringUtils.isBlank(token)) {
                // 有token则放行
                return chain.filter(exchange);
            }
        }

        // 没有token，返回错误提示信息
        Map<String, Object> errorMsg = new HashMap<>();
        errorMsg.put("code", "401");
        errorMsg.put("msg", "用户权限异常，请进行登录认证");
        errorMsg.put("success", false);

        // 设置响应头编码格式
        ServerHttpResponse response = exchange.getResponse();

        // writeWith()，将括号内的数据写入报文
        // Mono.just()，类似Stream.of()是数据流处理方法，将括号内DateBuffer类型数据进行流处理
        return response.writeWith(Mono.just(
                // 数据缓冲区，数据转换为流的中间过渡，数据 -> 数据缓冲区 -> 流
                // DataBufferFactory bufferFactory()，返回一个生成数据缓冲区的工厂
                // wrap()，生成新的数据缓冲区，并将括号内数据放入数据缓冲区，返回该数据缓冲区
                response.bufferFactory().wrap(JSON.toJSONBytes(errorMsg))
        ));
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
