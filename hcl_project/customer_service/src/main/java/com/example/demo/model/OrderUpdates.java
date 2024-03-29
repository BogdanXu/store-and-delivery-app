package com.example.demo.model;

import com.example.demo.enums.OrderStatus;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdates {
    private Long orderId;
    private OrderStatus status;
}
