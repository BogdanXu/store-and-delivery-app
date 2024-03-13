package com.example.demo.kafka;


import com.example.demo.enums.OrderStatus;
import com.example.demo.model.Order;
import com.example.demo.model.OrderUpdates;
import com.example.demo.service.ItemService;
import io.confluent.ksql.api.client.BatchedQueryResult;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.RoutingKafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class OrderConsumer {

    private final ItemService itemService;
    private final RoutingKafkaTemplate kafkaTemplate;
    private static final String DELIVERY_TOPIC = "delivery";
    private static final String ORDER_TOPIC = "order";
    private static final String ORDER_UPDATES_TOPIC = "order_updates";
    Logger logger = LoggerFactory.getLogger(OrderConsumer.class);


    @KafkaListener(topics = {"order"}, groupId = "spring-boot-kafka", properties = "value.deserializer:com.example.demo.serialization.OrderSerializer")
    public void consume(ConsumerRecord<Integer, Order> order) {
        Order receivedOrder = order.value();
        OrderUpdates orderUpdates = new OrderUpdates();

        //validate stock for each item in orderedItems
        for (Map.Entry<String, Integer> entry : receivedOrder.getOrderedItems().entrySet()) {
            Long itemId = Long.valueOf(entry.getKey());
            Integer quantity = entry.getValue();

            if (!itemService.isInStock(itemId, quantity)) {
                logger.error("Item with ID " + itemId + " is out of stock.");
                orderUpdates.setStatus(OrderStatus.INVALID);
            }
        }

        //send validated order if it's valid
        if(orderUpdates.getStatus()!=OrderStatus.INVALID){
            orderUpdates.setStatus(OrderStatus.READY_FOR_DELIVERY);
            orderUpdates.setOrderId(receivedOrder.getOrderId());
            System.out.println("Received order = " + receivedOrder.getOrderId() +
                    " with key " + order.key() +
                    " being sent at address " + receivedOrder.getAddress() +
                    " and being prepared for delivery.");
        }
        kafkaTemplate.send(ORDER_UPDATES_TOPIC, orderUpdates.getOrderId(), orderUpdates );
    }

    @EventListener(ApplicationStartedEvent.class)
    public void ksql_consume() {
        ClientOptions options = ClientOptions.create()
                .setHost("localhost")
                .setPort(8088);
        System.out.println("Starting to consume queries");
        Client client = Client.create(options);

        client.streamQuery("SELECT * FROM updated_orders EMIT CHANGES;")
                .thenAccept(streamedQueryResult -> {
                    System.out.println("Query has started. Query ID: " + streamedQueryResult.queryID());
                    OrderSubscriber subscriber = new OrderSubscriber();
                    streamedQueryResult.subscribe(subscriber);
                }).exceptionally(e -> {
                    System.out.println("Request failed: " + e);
                    return null;
                });
    }
}
