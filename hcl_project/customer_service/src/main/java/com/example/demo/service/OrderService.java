package com.example.demo.service;
import com.example.demo.dto.OrderDTO;
import com.example.demo.enums.OrderStatus;
import com.example.demo.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final KafkaTemplate<String, Order> template;

    public void sendOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setOrderedItems(orderDTO.getOrderedItems());
        order.setAddress(order.getAddress());
        order.setOrderId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
        order.setOrderTime(System.currentTimeMillis());
        order.setOrderStatus(OrderStatus.PLACED);

        template.send("order", order.getAddress(), order);
    }
}
