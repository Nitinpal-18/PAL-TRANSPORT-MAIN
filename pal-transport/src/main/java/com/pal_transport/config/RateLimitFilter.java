package com.pal_transport.config;

import com.pal_transport.application.service.SecurityAuditService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {
    
    private final RateLimitConfig rateLimitConfig;
    
    public RateLimitFilter(RateLimitConfig rateLimitConfig) {
        this.rateLimitConfig = rateLimitConfig;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String clientIp = getClientIpAddress(request);
        String endpoint = getEndpointKey(request);
        
        // Skip rate limiting for certain endpoints
        if (shouldSkipRateLimit(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        Bucket bucket = rateLimitConfig.resolveBucket(clientIp, endpoint);
        
        if (bucket.tryConsume(1)) {
            // Request allowed
            filterChain.doFilter(request, response);
        } else {
            // Rate limit exceeded - use a default wait time since getNanosToWaitForRefill is not available
            long waitTime = 60; // Default 60 seconds wait time
            
            log.warn("Rate limit exceeded for IP: {} on endpoint: {}", clientIp, endpoint);
            
            // Log rate limit violation (removed security audit to break circular dependency)
            log.info("RATE_LIMIT_EXCEEDED - Rate limit exceeded for IP {} on endpoint {}", clientIp, endpoint);
            
            // Set rate limit headers
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("X-RateLimit-Limit", String.valueOf(bucket.getAvailableTokens()));
            response.setHeader("X-RateLimit-Remaining", "0");
            response.setHeader("X-RateLimit-Reset", String.valueOf(waitTime));
            response.setHeader("Retry-After", String.valueOf(waitTime));
            
            // Send error response
            response.setContentType("application/json");
            response.getWriter().write(String.format(
                "{\"error\":\"Rate limit exceeded\",\"message\":\"Too many requests. Please try again in %d seconds.\",\"retryAfter\":%d}",
                waitTime, waitTime));
        }
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    private String getEndpointKey(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Create endpoint key based on path and method
        if (path.startsWith("/api/v1/auth/login")) {
            return "auth-login";
        } else if (path.startsWith("/api/v1/auth/google")) {
            return "auth-oauth";
        } else if (path.startsWith("/api/v1/auth/refresh")) {
            return "auth-refresh";
        } else if (path.startsWith("/api/v1/security/audit")) {
            return "security-audit";
        } else if (path.startsWith("/api/v1/trucks") && "POST".equals(method)) {
            return "trucks-create";
        } else if (path.startsWith("/api/v1/trucks") && "PUT".equals(method)) {
            return "trucks-update";
        } else {
            return "default";
        }
    }
    
    private boolean shouldSkipRateLimit(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Skip rate limiting for health checks and static resources
        return path.startsWith("/actuator/health") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/error") ||
               path.startsWith("/favicon.ico");
    }
} 