package com.example.demo.model;

import com.example.demo.enums.OrderStatus;
import lombok.*;

import java.io.Serializable;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdates {
    private Long orderId;
    private OrderStatus status;
}
