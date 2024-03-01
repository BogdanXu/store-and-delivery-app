package com.example.demo.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class DeliveryTopic {
    @Bean
    NewTopic delivery(){
        return TopicBuilder
                .name("delivery")
                .partitions(10)
                .replicas(1)
                .build();
    }
}
