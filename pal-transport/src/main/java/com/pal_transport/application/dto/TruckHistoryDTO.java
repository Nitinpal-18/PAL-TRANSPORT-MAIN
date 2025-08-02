package com.pal_transport.application.dto;

import com.pal_transport.application.enums.TruckStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TruckHistoryDTO {
    private Long id;
    private Long truckId;
    private Long orderId;
    private Long driverId;
    private LocalDate occupancyStartDate;
    private LocalDate occupancyEndDate;
    private Long tripDurationDays;
    private String remark;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private TruckStatus status;
}