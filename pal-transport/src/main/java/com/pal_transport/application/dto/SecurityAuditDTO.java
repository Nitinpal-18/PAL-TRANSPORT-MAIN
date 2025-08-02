package com.pal_transport.application.dto;

import com.pal_transport.application.entity.SecurityAudit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityAuditDTO {
    
    private Long id;
    private String eventType;
    private String eventDetails;
    private Long userId;
    private String userEmail;
    private String ipAddress;
    private String userAgent;
    private String requestMethod;
    private String requestUrl;
    private Integer responseStatus;
    private String sessionId;
    private SecurityAudit.RiskLevel riskLevel;
    private LocalDateTime createdAt;
    
    public static SecurityAuditDTO fromEntity(SecurityAudit entity) {
        return SecurityAuditDTO.builder()
                .id(entity.getId())
                .eventType(entity.getEventType())
                .eventDetails(entity.getEventDetails())
                .userId(entity.getUserId())
                .userEmail(entity.getUserEmail())
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .requestMethod(entity.getRequestMethod())
                .requestUrl(entity.getRequestUrl())
                .responseStatus(entity.getResponseStatus())
                .sessionId(entity.getSessionId())
                .riskLevel(entity.getRiskLevel())
                .createdAt(entity.getCreatedAt())
                .build();
    }
} 