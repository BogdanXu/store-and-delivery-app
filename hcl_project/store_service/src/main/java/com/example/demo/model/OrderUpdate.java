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
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use auto-increment
    private Long updateId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false) // Specify foreign key column and make it non-nullable
    private Order order; // Refer to the Order entity directly

    @Enumerated(EnumType.STRING) // Store status as a string
    private OrderStatus status;

    @Column(name = "update_time")
    private Timestamp updateTime;

}