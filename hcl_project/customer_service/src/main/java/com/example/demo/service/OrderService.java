package com.example.demo.service;

import com.example.demo.dto.OrderDTO;
import com.example.demo.enums.OrderStatus;
import com.example.demo.model.Order;
import com.example.demo.model.OrderUpdates;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.RoutingKafkaTemplate;
import org.springframework.stereotype.Service;

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

        //this needs a better key, order.getAddress() isn't a good one, but i have no other ideas now
        routingKafkaTemplate.send("order", order.getAddress(), order);
        routingKafkaTemplate.send("order_updates", String.valueOf(orderUpdates.getStatus()), orderUpdates);
    }
}
