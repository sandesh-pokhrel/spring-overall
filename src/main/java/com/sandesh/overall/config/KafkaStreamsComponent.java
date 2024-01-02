package com.sandesh.overall.config;

import com.sandesh.overall.model.Employee;
import com.sandesh.overall.model.Temperature;
import com.sandesh.overall.serde.EmployeeDeserializer;
import com.sandesh.overall.serde.EmployeeSerializer;
import com.sandesh.overall.serde.TemperatureDeserializer;
import com.sandesh.overall.serde.TemperatureSerializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sandesh.overall.avro.Vehicle;
import java.time.Duration;

@Component
public class KafkaStreamsComponent {

    @Autowired
    public void process(StreamsBuilder builder) {
        System.out.println("Dummy streams processing started");
        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();

        KStream<Long, String> textLines = builder.stream(KafkaConfig.NAMES_TOPIC_NAME, Consumed.with(longSerde, stringSerde));

        textLines.mapValues(val -> val.toUpperCase())
                .to(KafkaConfig.STREAMS_OUTPUT_TOPIC_NAME, Produced.with(longSerde, stringSerde));
    }

    @Autowired
    public void processTemperature(StreamsBuilder builder) {
        System.out.println("Temperature streams processing started");
        final Serde<String> stringSerde = Serdes.String();
        final Serde<Temperature> temperatureSerde = Serdes.serdeFrom(new TemperatureSerializer(), new TemperatureDeserializer());
        final Serde<Long> longSerde = Serdes.Long();

        KStream<Long, Temperature> temperatureStream = builder.stream(KafkaConfig.TEMPERATURE_INPUT_TOPIC_NAME, Consumed.with(longSerde, temperatureSerde));

        KTable<Long, String> temperatureTable =
                temperatureStream
                        .filter((key, temperature) -> temperature.getDegrees() > 60)
                        .map((key, temperature) -> KeyValue.pair(temperature.getDegrees(), "Alert : " + temperature.getDegrees()))
                        //.mapValues(temperature -> "Alert : " + temperature.getDegrees())
                        .toTable(Materialized.<Long, String, KeyValueStore<Bytes, byte[]>>as(KafkaConfig.TEMPERATURE_STORE)
                                .withKeySerde(longSerde)
                                .withValueSerde(stringSerde));


        temperatureTable.toStream()
                .to(KafkaConfig.TEMPERATURE_OUTPUT_TOPIC_NAME, Produced.with(longSerde, stringSerde));
    }

    @Autowired
    public void processEmployee(StreamsBuilder builder) {
        System.out.println("Employee streams processing started");
        final Serde<String> stringSerde = Serdes.String();
        final Serde<Employee> employeeSerde = Serdes.serdeFrom(new EmployeeSerializer(), new EmployeeDeserializer());
        final Serde<Long> longSerde = Serdes.Long();
        KStream<Long, Employee> employeeStream = builder.stream(KafkaConfig.EMPLOYEE_TOPIC_NAME, Consumed.with(longSerde, employeeSerde));
        KStream<Long, Employee> employee2Stream = builder.stream(KafkaConfig.EMPLOYEE_TOPIC_NAME_2, Consumed.with(longSerde, employeeSerde));

        ValueJoiner<Employee, Employee, String> joiner = (e1, e2) -> "Duplicate: " + e1.getName() + " - " + e2.getName();

        KStream<Long, String> combinedStream =
                employeeStream.join(employee2Stream, joiner, JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofMinutes(10)), StreamJoined.with(longSerde, employeeSerde, employeeSerde));

        combinedStream.to(KafkaConfig.EMPLOYEE_TOPIC_NAME_COMBINED, Produced.with(longSerde, stringSerde));
    }

    // @Autowired
    public void processAvro(StreamsBuilder builder) {
        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();
        KStream<Long, Vehicle> vehicleStream = builder.stream(KafkaConfig.VEHICLE_INPUT_TOPIC);
        vehicleStream.mapValues(val -> val.getName().toString().toUpperCase())
                .to(KafkaConfig.VEHICLE_OUTPUT_TOPIC, Produced.with(longSerde, stringSerde));
    }
}
