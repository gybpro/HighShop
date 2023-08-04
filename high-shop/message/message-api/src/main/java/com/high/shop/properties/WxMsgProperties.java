package com.high.shop.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "wx.msg")
public class WxMsgProperties {
    private String appId;
    private String appSecret;
    private String getTokenUrl;
    // private String getTokeType;
    private String templateId;
    private String sendMsgUrl;
    // private String sendMsgType;
    private Long refreshSeconds;
}
