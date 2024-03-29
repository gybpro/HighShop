package com.high.shop.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 短信验证配置
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "sms")
public class SmsProperties {
    private String accessKeyId;
    private String accessKeySecret;
    private String endpoint;
    private String signName;
    private String templateCode;
    private String templateParam;
}

