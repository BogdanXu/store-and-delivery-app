package com.example.demo.kafka;

import com.example.demo.serialization.OrderSerializer;
import com.example.demo.serialization.OrderUpdatesSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.RoutingKafkaTemplate;
import org.springframework.kafka.support.serializer.DelegatingByTopicSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Configuration
public class KafkaConfig {

    @Bean
    public RoutingKafkaTemplate routingTemplate(GenericApplicationContext context, ProducerFactory<Object, Object> pf) {
        // ProducerFactory with Order serializer
        Map<String, Object> configs = new HashMap<>(pf.getConfigurationProperties());
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                OrderSerializer.class);
        DefaultKafkaProducerFactory<Object, Object> orderPF =
                new DefaultKafkaProducerFactory<>(configs);
        context.registerBean("orderPF", DefaultKafkaProducerFactory.class, () -> orderPF);

        // ProducerFactory with OrderUpdates serializer
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                OrderUpdatesSerializer.class);
        DefaultKafkaProducerFactory<Object, Object> orderUpdatesPF =
                new DefaultKafkaProducerFactory<>(configs);

        Map<Pattern, ProducerFactory<Object, Object>> map = new LinkedHashMap<>();
        map.put(Pattern.compile("order"), orderPF);
        map.put(Pattern.compile("order_updates"), orderUpdatesPF);
        return new RoutingKafkaTemplate(map);
    }
}
