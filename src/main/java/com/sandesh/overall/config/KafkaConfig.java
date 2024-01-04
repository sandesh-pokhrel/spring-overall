package com.sandesh.overall.config;

import com.sandesh.overall.model.Employee;
import com.sandesh.overall.model.Temperature;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConfig {

    public static final String EMPLOYEE_TOPIC_NAME = "employee";
    public static final String EMPLOYEE_TOPIC_NAME_2 = "employee2";
    public static final String EMPLOYEE_TOPIC_NAME_COMBINED = "employee-combined";
    public static final String NAMES_TOPIC_NAME = "name";
    public static final String STREAMS_OUTPUT_TOPIC_NAME = "streams-output";
    public static final String TEMPERATURE_INPUT_TOPIC_NAME = "temperature-input";
    public static final String TEMPERATURE_OUTPUT_TOPIC_NAME = "temperature-output";
    public static final String VEHICLE_INPUT_TOPIC = "vehicle-input";
    public static final String VEHICLE_OUTPUT_TOPIC = "vehicle-output";
    public static final String TEMPERATURE_STORE = "temperature_store";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";

    @Bean
    public NewTopic employeeTopic() {
        return new NewTopic(EMPLOYEE_TOPIC_NAME, 3, (short) 1);
    }

    @Bean
    public NewTopic employeeTopicSecond() {
        return new NewTopic(EMPLOYEE_TOPIC_NAME_2, 3, (short) 1);
    }

    @Bean
    public NewTopic employeeTopicCombined() {
        return new NewTopic(EMPLOYEE_TOPIC_NAME_COMBINED, 3, (short) 1);
    }

    @Bean
    public NewTopic namesTopic() {
        return new NewTopic(NAMES_TOPIC_NAME, 3, (short) 1);
    }

    @Bean
    public NewTopic streamsOutputTopic() {
        return new NewTopic(STREAMS_OUTPUT_TOPIC_NAME, 3, (short) 1);
    }

    @Bean
    public NewTopic temperatureInputTopic() {
        return new NewTopic(TEMPERATURE_INPUT_TOPIC_NAME, 3, (short) 1);
    }

    @Bean
    public NewTopic temperatureOutputTopic() {
        return new NewTopic(TEMPERATURE_OUTPUT_TOPIC_NAME, 3, (short) 1);
    }

    @Bean
    public NewTopic vehicleInputTopic() {
        return new NewTopic(VEHICLE_INPUT_TOPIC, 3, (short) 1);
    }


    private ProducerFactory<Long, Employee> employeeProducerFactory() {
        Map<String, Object> configProps = genericProducerFactoryProps();
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    private ProducerFactory<Long, String> namesProducerFactory() {
        Map<String, Object> configProps = genericProducerFactoryProps();
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    private ProducerFactory<Long, Temperature> temperatureProducerFactory() {
        Map<String, Object> configProps = genericProducerFactoryProps();
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    private ConsumerFactory<Long, Temperature> temperatureConsumerFactory() {
        Map<String, Object> configProps = genericConsumerFactoryProps();
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    /*@Bean(name = "temperatureKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<Long, Temperature> temperatureKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, Temperature> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(temperatureConsumerFactory());
        return factory;
    }*/

    @Bean(name = "employeeKafkaTemplate")
    public KafkaTemplate<Long, Employee> employeeKafkaTemplate() {
        return new KafkaTemplate<>(employeeProducerFactory());
    }

    @Bean(name = "temperatureKafkaTemplate")
    public KafkaTemplate<Long, Temperature> temperatureKafkaTemplate() {
        return new KafkaTemplate<>(temperatureProducerFactory());
    }

    @Bean(name = "namesKafkaTemplate")
    public KafkaTemplate<Long, String> namesKafkaTemplate() {
        return new KafkaTemplate<>(namesProducerFactory());
    }

    @KafkaListener(topics = NAMES_TOPIC_NAME, id = "names_listener")
    public void listenNames(String message) {
        log.info("Received message: {}", message);
    }

    @KafkaListener(topics = STREAMS_OUTPUT_TOPIC_NAME, id = "names_output_listener")
    public void listenNamesFromOutput(String message) {
        log.info("Output message: {}", message);
    }

    @KafkaListener(topics = TEMPERATURE_INPUT_TOPIC_NAME, id = "temperature_input_listener"
            //containerFactory = "temperatureKafkaListenerContainerFactory"
            )
    public void listenTemperatureForInput(String temperature) {
        log.info("Input temperature degree: {}", temperature);
    }

    @KafkaListener(topics = TEMPERATURE_OUTPUT_TOPIC_NAME, id = "temperature_output_listener")
    public void listenTemperatureForOutput(String message) {
        log.info("Output temperature message: {}", message);
    }

    @KafkaListener(topics = EMPLOYEE_TOPIC_NAME_COMBINED, id = "emp_combined_output_listener")
    public void listenEmpCombinedForOutput(String message) {
        log.info("{}", message);
    }

    private Map<String, Object> genericProducerFactoryProps() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        return configProps;
    }

    private Map<String, Object> genericConsumerFactoryProps() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        return configProps;
    }
}
