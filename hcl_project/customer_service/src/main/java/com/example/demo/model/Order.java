package com.example.demo.model;

import com.example.demo.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long orderTime;
    private Long orderId;
    private Map<Long, Integer> orderedItems;
    private String address;
    private OrderStatus status;
}
