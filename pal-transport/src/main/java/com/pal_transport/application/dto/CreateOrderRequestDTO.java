package com.pal_transport.application.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CreateOrderRequestDTO {
    private ClientDTO client;
    private OrderDTO order;
    private TransportDTO transport;
    private PaymentDTO payment;
    private String driverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
