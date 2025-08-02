package com.pal_transport.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@ConfigurationProperties(prefix = "app.security.rate-limit")
@Data
public class RateLimitConfig {
    
    private Map<String, RateLimitSettings> endpoints = new ConcurrentHashMap<>();
    private RateLimitSettings defaultSettings = new RateLimitSettings();
    
    @Bean
    public Map<String, BucketConfiguration> bucketConfigurations() {
        Map<String, BucketConfiguration> configurations = new ConcurrentHashMap<>();
        
        // Default configuration
        configurations.put("default", createBucketConfiguration(defaultSettings));
        
        // Endpoint-specific configurations
        endpoints.forEach((endpoint, settings) -> {
            configurations.put(endpoint, createBucketConfiguration(settings));
        });
        
        return configurations;
    }
    
    private BucketConfiguration createBucketConfiguration(RateLimitSettings settings) {
        Bandwidth limit = Bandwidth.classic(settings.getRequests(), 
                Refill.intervally(settings.getRequests(), Duration.ofMinutes(settings.getWindowMinutes())));
        
        return BucketConfiguration.builder()
                .addLimit(limit)
                .build();
    }
    
    public Bucket resolveBucket(String key, String endpoint) {
        RateLimitSettings settings = endpoints.getOrDefault(endpoint, defaultSettings);
        
        Bandwidth limit = Bandwidth.classic(settings.getRequests(), 
                Refill.intervally(settings.getRequests(), Duration.ofMinutes(settings.getWindowMinutes())));
        
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
    
    @Data
    public static class RateLimitSettings {
        private int requests = 100; // requests per window
        private int windowMinutes = 1; // time window in minutes
        private boolean enabled = true;
    }
} 