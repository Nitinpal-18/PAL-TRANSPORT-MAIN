package com.pal_transport.application.dto;

import com.pal_transport.application.enums.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PaymentDTO {
    private Long id;
    private Long orderId;
    private Double paidAmount; // Amount paid by the user (renamed from amount)
    private Double totalAmount; // Total order amount for record keeping
    private String paymentMode;
    private String transactionNumber;
    private String nextPaymentDate;
    private Double pendingAmount;
    private PaymentStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String createdBy;
    private String lastModifiedBy;
    private Long version; // Updated pending amount
}
