package com.sandesh.overall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableKafka
@EnableScheduling
@EnableKafkaStreams
@SpringBootApplication
public class SpringOverallApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringOverallApplication.class, args);
    }

}
