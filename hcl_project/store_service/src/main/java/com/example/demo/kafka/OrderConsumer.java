package com.example.demo.kafka;


import com.example.demo.enums.OrderStatus;
import com.example.demo.model.Order;
import com.example.demo.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.net.SocketTimeoutException;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class OrderConsumer {

    private final ItemService itemService;
    private final KafkaTemplate<String, Order> kafkaTemplate;
    private static final String DELIVERY_TOPIC = "delivery";
    private static final String ORDER_TOPIC = "order";
    Logger logger = LoggerFactory.getLogger(OrderConsumer.class);


    @KafkaListener(topics = {"order"}, groupId = "spring-boot-kafka")
    public void consume(ConsumerRecord<Integer, Order> order) {
        Order receivedOrder = order.value();

        //validate stock for each item in orderedItems
        for (Map.Entry<Long, Integer> entry : receivedOrder.getOrderedItems().entrySet()) {
            Long itemId = entry.getKey();
            Integer quantity = entry.getValue();

            if (!itemService.isInStock(itemId, quantity)) {
                logger.error("Item with ID " + itemId + " is out of stock.");
                receivedOrder.setStatus(OrderStatus.INVALID);
            }
        }

        //send validated order if it's valid
        if(receivedOrder.getStatus()!=OrderStatus.INVALID){
            receivedOrder.setStatus(OrderStatus.READY_FOR_DELIVERY);
            System.out.println("Received order = " + receivedOrder.getOrderId() +
                    " with key " + order.key() +
                    " being sent at address " + receivedOrder.getAddress() +
                    " and being prepared for delivery.");
        }
        kafkaTemplate.send(DELIVERY_TOPIC, receivedOrder);
    }
}
