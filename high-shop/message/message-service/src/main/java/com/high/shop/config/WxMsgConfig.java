package com.high.shop.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.high.shop.properties.WxMsgProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 微信配置类：
 *  用于启动后，获取access_token
 *      方式一，当前类实现CommandLineRunner接口，重写run方法
 *      方式二，通过注解@PostConstruct，在启动后立即执行
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class WxMsgConfig {

    private final WxMsgProperties wxMsgProperties;

    private final RestTemplate restTemplate;

    private final StringRedisTemplate redisTemplate;

    public WxMsgConfig(WxMsgProperties wxMsgProperties, RestTemplate restTemplate, StringRedisTemplate redisTemplate) {
        this.wxMsgProperties = wxMsgProperties;
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void initWxAccessToken() {
        getWxAccessToken();
    }

    @Scheduled(initialDelay = 7000 * 1000, fixedDelay = 7000 * 1000)
    public void getWxAccessToken() {
        // 发送请求获取token
        String jsonStr = restTemplate.getForObject(
                String.format(
                        wxMsgProperties.getGetTokenUrl(),
                        wxMsgProperties.getAppId(),
                        wxMsgProperties.getAppSecret()
                ), String.class
        );

        JSONObject jsonObj = JSON.parseObject(jsonStr);

        if (jsonObj.containsKey("access_token") && jsonObj.containsKey("expires_in")) {
            String accessToken = jsonObj.getString("access_token");

            System.out.println(accessToken);

            // 获取token，存入redis
            redisTemplate.opsForValue().set("wx:msg:token", accessToken, wxMsgProperties.getRefreshSeconds(),
                    TimeUnit.SECONDS);
        }
    }

}
