package com.example.demo.dto;

import lombok.*;
import com.example.demo.enums.OrderStatus;
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateDTO {
    private Long orderId;
    private OrderStatus status;
}