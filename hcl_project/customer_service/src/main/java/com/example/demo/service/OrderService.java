package com.example.demo.service;
import com.example.demo.dto.OrderDTO;
import com.example.demo.enums.OrderStatus;
import com.example.demo.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.jms.AcknowledgeMode;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final KafkaTemplate<String, Order> template;

    public void sendOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setOrderedItems(orderDTO.getOrderedItems());
        order.setAddress(orderDTO.getAddress());
        order.setOrderId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
        order.setOrderTime(System.currentTimeMillis());
        order.setStatus(OrderStatus.PLACED);

        //this needs a better key, order.getAddress() isn't a good one, but i have no other ideas now
        template.send("order", order.getAddress(), order);
    }
}
