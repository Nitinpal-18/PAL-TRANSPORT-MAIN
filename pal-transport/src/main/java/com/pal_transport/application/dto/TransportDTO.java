package com.pal_transport.application.dto;

import com.pal_transport.application.enums.TransportSize;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TransportDTO {
    private Long id;
    private Long orderId;
    private Long clientId; // Added clientId field
    private String currentLocation;
    private String status;
    private AddressDTO sourceAddress;
    private AddressDTO destinationAddress;
    private TransportSize size;
    private String truckId;
    private double distance;
    private Long driverId;
    private double travelTime;
    private double petrolCost;
    private double miscellaneousExpenses;
    private double repairCharges;
    private LocalDate deliveryStartDate;
    private LocalDate estimatedDeliveryDate;
    private LocalDate actualDeliveryDate;
    private LocalDateTime lastUpdated;
}
