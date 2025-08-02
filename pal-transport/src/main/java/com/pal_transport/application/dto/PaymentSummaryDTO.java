package com.pal_transport.application.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PaymentSummaryDTO {
    private Double totalPendingAmount;
    private Long totalPendingOrders;
    private Double overdueAmount;
    private Long overdueOrders;
    private Double dueSoonAmount;
    private Long dueSoonOrders;
}