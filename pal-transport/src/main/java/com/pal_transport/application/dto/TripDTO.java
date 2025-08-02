package com.pal_transport.application.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TripDTO {
    private Long id;
    private Long orderId;
    private Long driverId;
    private double distance;
    private double travelTime;
    private double petrolCost;
    private double miscellaneousExpenses;
    private double repairCharges;
    private LocalDate estimatedDeliveryDate;
    private LocalDate actualDeliveryDate;
    private LocalDateTime lastUpdated;
    private LocalDate occupancyStartDate;
    private LocalDate occupancyEndDate;
}