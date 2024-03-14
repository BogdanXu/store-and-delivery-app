package com.example.demo.controller;

import com.example.demo.service.OrderService;
import com.example.demo.dto.OrderDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping()
    public ResponseEntity<String> placeOrder(@Valid @RequestBody OrderDTO orderDTO) {
        orderService.sendOrder(orderDTO);
        System.out.println("Order placed successfully");
        return ResponseEntity.ok("Order placed successfully");
    }
}