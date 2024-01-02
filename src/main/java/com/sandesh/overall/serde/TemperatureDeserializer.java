package com.sandesh.overall.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandesh.overall.model.Temperature;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.kafka.support.serializer.DeserializationException;

import java.nio.charset.StandardCharsets;

public class TemperatureDeserializer implements Deserializer<Temperature> {

    @Override
    public Temperature deserialize(String s, byte[] bytes) {
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (bytes == null){
                System.out.println("Null received at deserializing");
                return null;
            }
            return objectMapper.readValue(new String(bytes, StandardCharsets.UTF_8), Temperature.class);
        } catch (Exception e) {
            throw new DeserializationException("Error when deserializing byte[] to Temperature", bytes, false, e);
        }
    }
}
