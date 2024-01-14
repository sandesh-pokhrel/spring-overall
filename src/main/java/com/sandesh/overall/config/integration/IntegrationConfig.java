package com.sandesh.overall.config.integration;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.core.GenericSelector;
import org.springframework.integration.core.GenericTransformer;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.PollerFactory;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Configuration
@IntegrationComponentScan
public class IntegrationConfig {

    // @Bean
    public ApplicationRunner runner(IntegrationFlowContext context) {
        return (args) -> {
            IntegrationFlow sourceFlow = sourceFlow(1L);
            IntegrationFlow destinationFlow = destinationFlow();
            Set.of(destinationFlow, sourceFlow).forEach(flow -> context.registration(flow).register().start());
        };
    }

    @Bean
    public ApplicationRunner greetingRunner(GreetingGateway greetingGateway) {
        return (args) -> {
            for (int i=0; i<10; i++) {
                greetingGateway.greet("Hello " + ThreadLocalRandom.current().nextInt(100, 999));
            }
        };
    }

    @Bean
    public MessageChannel atob() {
        return MessageChannels.direct().getObject();
    }

    @Bean
    public MessageChannel greet() {
        return MessageChannels.direct().getObject();
    }

    // @Bean // If no annotation then run using IntegrationFlowContext
    public IntegrationFlow sourceFlow(long pollSeconds) {
        return IntegrationFlow
                .from((MessageSource<String>) () -> MessageBuilder.withPayload("Hello at " + System.currentTimeMillis()).build(),
                        poller -> poller.poller(pm -> PollerFactory.fixedRate(Duration.of(pollSeconds, ChronoUnit.SECONDS))))
                .transform((GenericTransformer<String, String>) String::toUpperCase)
                .handle((GenericHandler<String>) (payload, headers) -> {
                    System.out.println("Handler ::: " + payload);
                    return payload;
                })
                .filter((GenericSelector<String>) source -> source.startsWith("HEL"))
                .channel(atob()).get();
    }

    // @Bean // If no annotation then run using IntegrationFlowContext
    public IntegrationFlow destinationFlow() {
        return IntegrationFlow
                .from(atob())
                .handle((GenericHandler<String>) (payload, headers) -> {
                    System.out.println("Received ::: " + payload);
                    return null;
                }).get();
    }

    @Bean
    public IntegrationFlow gatewayFlow() {
        return IntegrationFlow
                .from(greet())
                .handle((GenericHandler<String>) (payload, headers) -> {
                    System.out.println("Received from Gateway ::: " + payload);
                    return null;
                }).get();
    }
}
