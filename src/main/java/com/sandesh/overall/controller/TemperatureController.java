package com.sandesh.overall.controller;

import com.github.javafaker.Faker;
import com.sandesh.overall.config.KafkaConfig;
import com.sandesh.overall.model.Temperature;
import com.sandesh.overall.util.GenericUtil;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.stream.IntStream;

@RestController
public class TemperatureController {

    private final KafkaTemplate<Long, Temperature> kafkaTemplate;
    private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    public TemperatureController(@Qualifier("temperatureKafkaTemplate") KafkaTemplate<Long, Temperature> kafkaTemplate,
                                 StreamsBuilderFactoryBean streamsBuilderFactoryBean) {
        this.kafkaTemplate = kafkaTemplate;
        this.streamsBuilderFactoryBean = streamsBuilderFactoryBean;
    }

    @GetMapping("/generate-temps")
    public void generateTemperatures() {
        final Faker faker = new Faker();
        final Random random = new Random();

        IntStream.range(1, 10).forEach(val -> {
            kafkaTemplate.send(KafkaConfig.TEMPERATURE_INPUT_TOPIC_NAME,
                    new Temperature(random.nextLong(100) + 1, faker.job().title()));
            GenericUtil.sleep(1000);
        });
    }

    @RequestMapping("/get-store/{degree}")
    public String getStore(@PathVariable Long degree) {
        KafkaStreams kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();
        assert kafkaStreams != null;
        ReadOnlyKeyValueStore<Long, String> counts =
                kafkaStreams.store(StoreQueryParameters.fromNameAndType(KafkaConfig.TEMPERATURE_STORE, QueryableStoreTypes.keyValueStore()));
        return counts.get(degree);
    }
}
