package com.pal_transport.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {
    
    private Jwt jwt = new Jwt();
    private GoogleOAuth googleOAuth = new GoogleOAuth();
    private Cors cors = new Cors();
    
    @Data
    public static class Jwt {
        private String secret = "your-256-bit-secret-key-here-change-in-production";
        private long expirationMs = 86400000; // 24 hours
        private long refreshExpirationMs = 604800000; // 7 days
        private String issuer = "pal-transport";
        private String audience = "pal-transport-users";
    }
    
    @Data
    public static class GoogleOAuth {
        private String clientId = "4536067250-cruj6vbtlhhdvrktova725fssla3k5hu.apps.googleusercontent.com";
        private String clientSecret = "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";
        private String redirectUri = "http://localhost:8081/auth/callback";
        private String scope = "openid email profile";
    }
    
    @Data
    public static class Cors {
        private String[] allowedOrigins = {"http://localhost:8081", "http://localhost:3000"};
        private String[] allowedMethods = {"GET", "POST", "PUT", "DELETE", "OPTIONS"};
        private String[] allowedHeaders = {"*"};
        private boolean allowCredentials = true;
        private long maxAge = 3600; // 1 hour
    }
} 