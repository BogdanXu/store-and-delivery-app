package com.example.demo.model;

import lombok.*;
import com.example.demo.enums.OrderStatus;
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdates {
    private Long orderId;
    private OrderStatus status;
}
