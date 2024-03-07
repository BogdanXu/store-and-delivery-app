package com.example.demo.kafka;

import com.example.demo.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.RoutingKafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
public class OrderProducer {
    private final RoutingKafkaTemplate routingKafkaTemplate;

    public static Order generateRandomOrder() {
        Random random = new Random();
        return Order.builder()
                .orderTime(System.currentTimeMillis())
                .orderId(Math.abs(random.nextLong()))
                .orderedItems(generateRandomOrderedItems(random))
                .address("Street " + (random.nextInt(100) + 1) + ", Iasi")
                .build();
    }

    private static Map<String, Integer> generateRandomOrderedItems(Random random) {
        Map<String, Integer> orderedItems = new HashMap<>();
        int itemCount = random.nextInt(5) + 1;
        for (int i = 0; i < itemCount; i++) {
            orderedItems.put(String.valueOf(random.nextLong() % 10), Math.abs(random.nextInt(10)));
        }
        return orderedItems;
    }

    // @EventListener(ApplicationStartedEvent.class)
    // decomment this to generate orders
    public void generate() {
        Order order = generateRandomOrder();
        final Flux<String> stringFlux = Flux.fromStream(Stream.generate(order::toString));
        final Flux<Long> interval = Flux.interval(Duration.ofMillis(1000));

        Flux.zip(interval, stringFlux)
                .map(it -> routingKafkaTemplate.send("order", "FanCourier", generateRandomOrder()))
                .blockLast();
    }

}
