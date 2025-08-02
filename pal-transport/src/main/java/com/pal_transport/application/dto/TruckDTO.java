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
public class TruckDTO {
    private Long id;
    private String name;
    private String type;
    private String truckNumber;
    private TruckStatus status;
    private String truckSize;
    private int capacity;
    private String location;
    private String imageUrl;
    private Long orderId;
    private Long driverId;
    private String maintenanceStatus;
    private LocalDate nextMaintenanceDate;
    private LocalDate registrationExpiry;
    private LocalDate insuranceExpiry;
    private double mileage;
    private double averageSpeed;
    private LocalDate occupancyStartDate;
    private LocalDate occupancyEndDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
