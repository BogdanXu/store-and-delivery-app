package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import com.example.demo.enums.OrderStatus;

import java.sql.Timestamp;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_update")
public class OrderUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long updateId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "update_time")
    private Timestamp updateTime;

}