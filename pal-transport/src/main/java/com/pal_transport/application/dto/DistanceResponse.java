package com.pal_transport.application.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DistanceResponse {
    private double totalDistance;  // in kilometers
    private double margin;         // computed margin in km
    private double lowerBound;     // totalDistance - margin
    private double upperBound;     // totalDistance + margin
}
