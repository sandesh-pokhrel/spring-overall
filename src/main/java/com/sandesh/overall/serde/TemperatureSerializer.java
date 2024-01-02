package com.sandesh.overall.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandesh.overall.model.Temperature;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class TemperatureSerializer implements Serializer<Temperature> {

    @Override
    public byte[] serialize(String s, Temperature temperature) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            if (temperature == null) {
                System.out.println("Null received at serializing");
                return null;
            }
            return objectMapper.writeValueAsBytes(temperature);
        } catch (Exception e) {
            throw new SerializationException("Error when serializing Temperature to byte[]");
        }
    }
}
