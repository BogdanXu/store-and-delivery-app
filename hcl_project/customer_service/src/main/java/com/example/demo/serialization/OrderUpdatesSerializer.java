package com.example.demo.serialization;

import com.example.demo.model.OrderUpdates;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;
import java.util.Map;

public class OrderUpdatesSerializer implements Serializer<OrderUpdates>, Deserializer<OrderUpdates> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, OrderUpdates data) {
        try {
            //writeValueAsBytes converts into *JSON byte array!!*, the order topic stores Order objects as JSON data
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Error serializing Order", e);
        }
    }

    @Override
    public OrderUpdates deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, OrderUpdates.class);
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
