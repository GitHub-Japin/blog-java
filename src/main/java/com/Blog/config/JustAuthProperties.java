package com.Blog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "justauth.config.gitee")
public class JustAuthProperties {

    private String clientId;

    private String clientSecret;

    private String redirectUri;
}
