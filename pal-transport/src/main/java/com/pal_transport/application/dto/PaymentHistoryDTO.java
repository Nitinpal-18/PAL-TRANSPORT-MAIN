package com.pal_transport.application.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PaymentHistoryDTO {
    private Long id;
    private Long orderId;
    private Double paidAmount;
    private String paymentMode;
    private String transactionId;
    private LocalDateTime paymentDate;
    private Double remainingAmount;
    private String remarks;
    private LocalDateTime createdDate;
    private String createdBy;
}