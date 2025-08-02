package com.pal_transport.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityEventDTO {
    
    @NotBlank(message = "Event type is required")
    private String event;
    
    @NotBlank(message = "Event details are required")
    private String details;
    
    private String userId;
    private String userEmail;
    private String ipAddress;
    private String userAgent;
} 