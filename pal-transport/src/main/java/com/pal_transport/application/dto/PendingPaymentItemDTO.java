package com.pal_transport.application.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PendingPaymentItemDTO {
    private Long orderId;
    private String orderNumber;
    private String customerName;
    private LocalDate dueDate;
    private Double paidAmount; // Updated: Use 'paidAmount' to match entity field
    private String status; // OVERDUE, DUE_SOON, PENDING, PARTIAL, COMPLETE
    private String paymentMode;
    private Long daysDue; // Negative for overdue, positive for upcoming
}