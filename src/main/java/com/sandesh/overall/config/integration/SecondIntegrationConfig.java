package com.sandesh.overall.config.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.handler.annotation.Payload;

@RequiredArgsConstructor
public class SecondIntegrationConfig {

    private final GreetingGateway greetingGateway;
    private static final String UPPERCASE_IN_CHANNEL = "uppercaseIn";
    private static final String UPPERCASE_OUT_CHANNEL = "uppercaseOut";

    // @Bean
    public ApplicationRunner applicationRunner() {
        return args -> greetingGateway.uppercaseIn("lowercase name");
    }

    @ServiceActivator(inputChannel = UPPERCASE_OUT_CHANNEL)
    public void upperCase(@Payload String payload) {
        System.out.println("Incoming payload ::: " + payload);
    }

    @Transformer(inputChannel = UPPERCASE_IN_CHANNEL, outputChannel = UPPERCASE_OUT_CHANNEL)
    public String uppercaseTransformer(@Payload String payload) {
        String transformedPayload = payload.toUpperCase();
        System.out.println("Upper cased payload ::: " + transformedPayload);
        return transformedPayload;
    }
}
