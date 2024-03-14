package com.example.demo.kafka;

import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.OrderUpdateDTO;
import com.example.demo.model.OrderUpdate;
import com.example.demo.service.ItemService;
import com.example.demo.service.OrderService;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import lombok.RequiredArgsConstructor;
import com.example.demo.model.Order;
import com.example.demo.enums.OrderStatus;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.RoutingKafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class OrderConsumer {

    private final ItemService itemService;
    private final OrderService orderService;
    private final RoutingKafkaTemplate kafkaTemplate;
    private static final String DELIVERY_TOPIC = "delivery";
    private static final String ORDER_TOPIC = "order";
    private static final String ORDER_UPDATES_TOPIC = "order_updates";
    Logger logger = LoggerFactory.getLogger(OrderConsumer.class);


    @KafkaListener(topics = {"order"}, groupId = "spring-boot-kafka", properties = "value.deserializer:com.example.demo.serialization.OrderSerializer")
    @ConditionalOnProperty(prefix = "order", name = "save", havingValue = "false")
    public void consumeWithoutOrderJPA(ConsumerRecord<Integer, OrderDTO> order) {
        OrderDTO receivedOrder = order.value();
        OrderUpdateDTO orderUpdate = new OrderUpdateDTO();

        //validate stock for each item in orderedItems
        for (Map.Entry<String, Integer> entry : receivedOrder.getOrderedItems().entrySet()) {
            Long itemId = Long.valueOf(entry.getKey());
            Integer quantity = entry.getValue();

            if (!itemService.isInStock(itemId, quantity)) {
                logger.error("Item with ID " + itemId + " is out of stock.");
                orderUpdate.setStatus(OrderStatus.INVALID);
            }
        }

        //send validated order if it's valid
        if(orderUpdate.getStatus()!=OrderStatus.INVALID){
            orderUpdate.setStatus(OrderStatus.READY_FOR_DELIVERY);
            orderUpdate.setOrderId(receivedOrder.getOrderId());
            System.out.println("Received order = " + receivedOrder.getOrderId() +
                    " with key " + order.key() +
                    " being sent at address " + receivedOrder.getAddress() +
                    " and being prepared for delivery.");
        }
        kafkaTemplate.send(ORDER_UPDATES_TOPIC, orderUpdate.getOrderId(), orderUpdate );
    }

    @KafkaListener(topics = {"order"}, groupId = "spring-boot-kafka", properties = "value.deserializer:com.example.demo.serialization.OrderSerializer")
    @ConditionalOnProperty(prefix = "order", name = "save", havingValue = "true")
    public void consumeWithOrderJPA(ConsumerRecord<Integer, OrderDTO> order) {
        OrderDTO receivedOrder = order.value();
        //this is debatable:
        //1 - I don't actually listen to the order_updates topic, because when a new order message is sent to the order topic, I can assume its status will always be PLACED
        //2 - In order to do this properly, I'd have to check whether the received order id has status PLACED in order_updates
        OrderUpdateDTO orderUpdateDTO = new OrderUpdateDTO(receivedOrder.getOrderId(), OrderStatus.PLACED);

        Order savedOrder = orderService.saveOrder(receivedOrder);
        OrderUpdate savedOrderUpdate = orderService.saveOrderUpdate(savedOrder, orderUpdateDTO);

        //validate stock for each item in orderedItems
        for (Map.Entry<String, Integer> entry : receivedOrder.getOrderedItems().entrySet()) {
            Long itemId = Long.valueOf(entry.getKey());
            Integer quantity = entry.getValue();

            if (!itemService.isInStock(itemId, quantity)) {
                logger.error("Item with ID " + itemId + " is out of stock.");
                orderUpdateDTO.setStatus(OrderStatus.INVALID);
                orderService.saveOrderUpdate(savedOrder, orderUpdateDTO);
            }
        }

        //save and send validated order_update if it's valid
        if(orderUpdateDTO.getStatus()!=OrderStatus.INVALID){
            orderUpdateDTO.setStatus(OrderStatus.READY_FOR_DELIVERY);
            orderUpdateDTO.setOrderId(receivedOrder.getOrderId());
            System.out.println("Received order = " + receivedOrder.getOrderId() +
                    " with key " + order.key() +
                    " being sent at address " + receivedOrder.getAddress() +
                    ", will be prepared for delivery.");
            orderService.saveOrderUpdate(savedOrder, orderUpdateDTO);
        }
        kafkaTemplate.send(ORDER_UPDATES_TOPIC, orderUpdateDTO.getOrderId(), orderUpdateDTO);
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
