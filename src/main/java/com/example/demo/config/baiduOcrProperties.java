package com.example.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "baidu.ocr")
public class baiduOcrProperties {
    // 百度OCR的App ID
    private String appId;
    // 百度OCR的API Key
    private String apiKey;
    // 百度OCR的Secret Key
    private String secretKey;
}
