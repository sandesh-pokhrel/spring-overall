package com.sandesh.overall.config.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Payload;

@RequiredArgsConstructor
public class SecondIntegrationConfig {

    private final GreetingGateway greetingGateway;

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> greetingGateway.uppercaseIn("lowercase name");
    }

    @ServiceActivator(inputChannel = "uppercaseIn")
    public String upperCase(@Payload String payload) {
        System.out.println("Incoming payload ::: " + payload);
        return payload.toUpperCase();
    }
}
