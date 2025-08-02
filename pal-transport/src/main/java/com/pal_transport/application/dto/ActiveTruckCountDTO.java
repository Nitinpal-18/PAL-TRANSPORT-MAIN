package com.pal_transport.application.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ActiveTruckCountDTO {
    private long currentWeekCount;
    private long previousWeekCount;
    private String currentWeekRange;
    private String previousWeekRange;
}