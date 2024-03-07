package com.example.demo.model;

import lombok.*;

import java.io.Serializable;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    private Long orderTime;
    private Long orderId;
    private Map<String, Integer> orderedItems;
    private String address;
}
