package com.example.demo.kafka;


import com.example.demo.enums.OrderStatus;
import com.example.demo.model.Order;
import com.example.demo.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class OrderConsumer {

    private final ItemService itemService;
    private final KafkaTemplate<String, Order> kafkaTemplate;
    private static final String DELIVERY_TOPIC = "delivery";


    @KafkaListener(topics = {"order"}, groupId = "spring-boot-kafka")
    public void consume(ConsumerRecord<Integer, Order> order) {
        Order receivedOrder = order.value();

        //validate stock for each item in orderedItems
        for (Map.Entry<Long, Integer> entry : receivedOrder.getOrderedItems().entrySet()) {
            Long itemId = entry.getKey();
            Integer quantity = entry.getValue();

            if (!itemService.isInStock(itemId, quantity)) {
                throw new RuntimeException("Item with ID " + itemId + " is out of stock.");
            }
        }

        //send validated order to deliveryTopic
        receivedOrder.setStatus(OrderStatus.READY);
        kafkaTemplate.send(DELIVERY_TOPIC, receivedOrder);

        System.out.println("Received order = " + receivedOrder.getOrderId() +
                " with key " + order.key() +
                " being sent at address " + receivedOrder.getAddress() +
                " and redirected to delivery topic.");
    }
}
