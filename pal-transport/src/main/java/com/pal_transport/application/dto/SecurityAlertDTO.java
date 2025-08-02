package com.pal_transport.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityAlertDTO {
    
    private String alertType;
    private String message;
    private String details;
    private Severity severity;
    private LocalDateTime timestamp;
    private String userId;
    private String ipAddress;
    private String userAgent;
    private String requestUrl;
    private String requestMethod;
    
    public enum Severity {
        LOW, MEDIUM, HIGH, CRITICAL, INFO
    }
    
    public static SecurityAlertDTO createLoginFailedAlert(String userEmail, String ipAddress, String userAgent) {
        return SecurityAlertDTO.builder()
                .alertType("LOGIN_FAILED")
                .message("Failed login attempt detected")
                .details("User: " + userEmail)
                .severity(Severity.HIGH)
                .timestamp(LocalDateTime.now())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
    }
    
    public static SecurityAlertDTO createRateLimitAlert(String ipAddress, String endpoint, String userAgent) {
        return SecurityAlertDTO.builder()
                .alertType("RATE_LIMIT_EXCEEDED")
                .message("Rate limit exceeded")
                .details("IP: " + ipAddress + ", Endpoint: " + endpoint)
                .severity(Severity.MEDIUM)
                .timestamp(LocalDateTime.now())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
    }
    
    public static SecurityAlertDTO createUnauthorizedAccessAlert(String ipAddress, String endpoint, String userAgent) {
        return SecurityAlertDTO.builder()
                .alertType("UNAUTHORIZED_ACCESS")
                .message("Unauthorized access attempt")
                .details("IP: " + ipAddress + ", Endpoint: " + endpoint)
                .severity(Severity.CRITICAL)
                .timestamp(LocalDateTime.now())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .requestUrl(endpoint)
                .build();
    }
    
    public static SecurityAlertDTO createSuspiciousActivityAlert(String activity, String details, String ipAddress) {
        return SecurityAlertDTO.builder()
                .alertType("SUSPICIOUS_ACTIVITY")
                .message("Suspicious activity detected: " + activity)
                .details(details)
                .severity(Severity.HIGH)
                .timestamp(LocalDateTime.now())
                .ipAddress(ipAddress)
                .build();
    }
    
    public static SecurityAlertDTO createSystemAlert(String message, String details) {
        return SecurityAlertDTO.builder()
                .alertType("SYSTEM_ALERT")
                .message(message)
                .details(details)
                .severity(Severity.INFO)
                .timestamp(LocalDateTime.now())
                .build();
    }
} 