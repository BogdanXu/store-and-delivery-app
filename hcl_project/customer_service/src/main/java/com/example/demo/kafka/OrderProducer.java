package com.example.demo.kafka;

import com.example.demo.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final KafkaTemplate<String, Order> template;

    public static Order generateRandomOrder() {
        Order order = new Order();
        Random random = new Random();

        order.setOrderTime(System.currentTimeMillis());
        order.setOrderId(Math.abs(random.nextLong()));

        Map<Long, Integer> orderedItems = new HashMap<>();
        int itemCount = random.nextInt(5) + 1;
        for (int i = 0; i < itemCount; i++) {
            orderedItems.put(random.nextLong() % 10, Math.abs(random.nextInt(10)));
        }
        order.setOrderedItems(orderedItems);
        order.setAddress("Street " + (random.nextInt(100) + 1) + ", Iasi");

        return order;
    }

//    @EventListener(ApplicationStartedEvent.class)
    //decomment this to generate orders
    public void generate(){
        Order order = generateRandomOrder();
        final Flux<String> stringFlux = Flux.fromStream(Stream.generate(order::toString));
        final Flux<Long> interval = Flux.interval(Duration.ofMillis(1000));

        Flux.zip(interval, stringFlux)
                .map(it -> template.send("order", "FanCourier", generateRandomOrder()))
                .blockLast();
    }

}
