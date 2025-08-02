package com.pal_transport.application.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PaymentDashboardDTO {
    private PaymentSummaryDTO summary;
    private List<PendingPaymentItemDTO> pendingPayments;
}