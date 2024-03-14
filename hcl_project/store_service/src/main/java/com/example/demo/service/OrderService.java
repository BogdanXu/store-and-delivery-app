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
        Order order = new Order(orderDTO.getOrderTime(), orderDTO.getOrderId(), orderDTO.getOrderedItems(), orderDTO.getAddress());
        return orderRepository.save(order);
    }

    public OrderUpdate saveOrderUpdate(Order order, OrderUpdateDTO orderUpdateDTO){
        OrderUpdate orderUpdate = new OrderUpdate(null, order, orderUpdateDTO.getStatus(), new Timestamp(System.currentTimeMillis()));
        return orderUpdateRepository.save(orderUpdate);
    }
}
