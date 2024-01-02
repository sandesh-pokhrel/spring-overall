package com.sandesh.overall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class KafkaStreamBinderConfig {

    @Bean
    public Function<String, String> toUpperCase() {
        System.out.println("Doing something");
        return String::toUpperCase;
    }
}
