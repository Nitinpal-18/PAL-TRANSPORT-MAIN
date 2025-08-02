package com.pal_transport.application.dto;

import com.pal_transport.application.enums.OrderStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OrderDTO {
    private Long id;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Long clientId;
    private Long transportId;
    private Long driverId;
    private Long paymentId;
    private Long billId;

}
