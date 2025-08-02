package com.pal_transport.application.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OutstandingAmountDTO {
    private Double totalOutstandingAmount;
    private Long totalOutstandingOrders;
    private Double overdueAmount;
    private Long overdueOrders;
}