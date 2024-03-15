package com.example.demo.service;


import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.OrderUpdateDTO;
import com.example.demo.model.Order;
import com.example.demo.model.OrderUpdate;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.OrderUpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderUpdateRepository orderUpdateRepository;

    public Order saveOrder(OrderDTO orderDTO){
        Order order = Order.builder()
                .orderTime(System.currentTimeMillis())
                .orderId(orderDTO.getOrderId())
                .orderedItems(orderDTO.getOrderedItems())
                .address(orderDTO.getAddress())
                .build();
        return orderRepository.save(order);
    }

    public OrderUpdate saveOrderUpdate(Order order, OrderUpdateDTO orderUpdateDTO){
        OrderUpdate orderUpdate = OrderUpdate.builder()
                .order(order)
                .status(orderUpdateDTO.getStatus())
                .updateTime(new Timestamp(System.currentTimeMillis()))
                .build();
        return orderUpdateRepository.save(orderUpdate);
    }
}
