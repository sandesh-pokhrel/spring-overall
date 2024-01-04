package com.sandesh.overall.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandesh.overall.model.Employee;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.kafka.support.serializer.DeserializationException;

import java.nio.charset.StandardCharsets;

public class EmployeeDeserializer implements Deserializer<Employee> {

    @Override
    public Employee deserialize(String s, byte[] bytes) {
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (bytes == null){
                System.out.println("Null received at deserializing");
                return null;
            }
            return objectMapper.readValue(new String(bytes, StandardCharsets.UTF_8), Employee.class);
        } catch (Exception e) {
            throw new DeserializationException("Error when deserializing byte[] to Employee", bytes, false, e);
        }
    }
}
