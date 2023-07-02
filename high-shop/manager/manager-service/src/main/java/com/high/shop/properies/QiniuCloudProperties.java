package com.high.shop.properies;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "qiniu")
public class QiniuCloudProperties {
    private String resource;
    private String bucketName;
    private String accessKey;
    private String accessSecret;
}
