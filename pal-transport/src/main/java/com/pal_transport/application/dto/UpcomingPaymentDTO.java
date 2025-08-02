package com.pal_transport.application.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpcomingPaymentDTO {
    private Long orderId;
    private Double pendingAmount;
    private LocalDate nextPaymentDate;
    private String paymentMode;
    private Long daysDue;
}