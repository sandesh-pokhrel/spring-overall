package com.sandesh.overall.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandesh.overall.model.Employee;
import com.sandesh.overall.model.Temperature;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class EmployeeSerializer implements Serializer<Employee> {

    @Override
    public byte[] serialize(String s, Employee employee) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            if (employee == null) {
                System.out.println("Null received at serializing");
                return null;
            }
            return objectMapper.writeValueAsBytes(employee);
        } catch (Exception e) {
            throw new SerializationException("Error when serializing Employee to byte[]");
        }
    }
}
