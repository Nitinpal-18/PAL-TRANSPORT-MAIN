package com.pal_transport.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.properties.images")
@Data
public class ImageProperties {
    private String path;
    private String url;
}
