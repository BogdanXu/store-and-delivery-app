package com.example.demo.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class OrderTopic {
    @Bean
    NewTopic order(){
        return TopicBuilder
                .name("order")
                .partitions(10)
                .replicas(1)
                .build();
    }
    @Bean
    NewTopic orderUpdates(){
        return TopicBuilder
                .name("order_updates")
                .partitions(10)
                .replicas(1)
                .build();
    }
    @Bean
    NewTopic latestStatuses(){
        return TopicBuilder
                .name("latest_statuses")
                .partitions(10)
                .replicas(1)
                .build();
    }
}
