package com.example.demo.serialization;

import com.example.demo.model.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;
import java.util.Map;

public class OrderSerializer implements Serializer<Order>, Deserializer<Order> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, Order data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Error serializing Order", e);
        }
    }

    @Override
    public Order deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, Order.class);
        } catch (IOException e) {
            throw new SerializationException("Error deserializing Order", e);
        }
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // No additional configuration needed
    }

    @Override
    public void close() {
        // No resources to close
    }
}