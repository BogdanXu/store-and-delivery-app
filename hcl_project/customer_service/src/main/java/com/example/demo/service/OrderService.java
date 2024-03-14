package com.example.demo.service;

import com.example.demo.model.Order;
import com.example.demo.model.OrderUpdates;
import com.example.demo.dto.OrderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.RoutingKafkaTemplate;
import org.springframework.stereotype.Service;
import com.example.demo.enums.OrderStatus;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final RoutingKafkaTemplate routingKafkaTemplate;

    public void sendOrder(OrderDTO orderDTO) {
        Order order = Order.builder()
                .orderId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE)
                .orderTime(System.currentTimeMillis())
                .orderedItems(orderDTO.getOrderedItems())
                .address(orderDTO.getAddress())
                .build();

        OrderUpdates orderUpdates = OrderUpdates.builder()
                .orderId(order.getOrderId())
                .status(OrderStatus.PLACED)
                .build();

        routingKafkaTemplate.send("order", order.getOrderId(), order);
        routingKafkaTemplate.send("order_updates", orderUpdates.getOrderId(), orderUpdates);
    }
}
