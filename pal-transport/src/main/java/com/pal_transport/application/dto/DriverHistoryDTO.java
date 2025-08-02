package com.pal_transport.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverHistoryDTO {
    private Long id;
    
    @NotNull(message = "Driver ID cannot be null")
    private Long driverId;
    
    private Long orderId;
    private LocalDate occupancyStartDate;
    private LocalDate occupancyEndDate;
    
    @NotNull(message = "Status cannot be null")
    private String status;
    
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
