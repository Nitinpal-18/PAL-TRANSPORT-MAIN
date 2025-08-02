package com.pal_transport.application.dto;

import lombok.*;
import java.time.LocalDate;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientSearchDTO {
    private String companyName;
    private String contactEmail;
    private String contactManager;
    private LocalDate lastOrderDateFrom;
    private LocalDate lastOrderDateTo;
    private Integer minOrders;
    private Integer maxOrders;
    private String city;
    private String state;
}