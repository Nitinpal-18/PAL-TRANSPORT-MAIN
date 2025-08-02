package com.pal_transport.application.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientStatsDTO {
    private Long totalClients;
    private Long activeClients;
    private Long clientsWithOrders;
    private Double averageOrdersPerClient;
    private Long totalAddresses;
}