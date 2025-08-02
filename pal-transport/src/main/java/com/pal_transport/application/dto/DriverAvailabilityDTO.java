package com.pal_transport.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverAvailabilityDTO {
    private DriverDTO driver;
    private boolean available;
    private String unavailabilityReason;
}