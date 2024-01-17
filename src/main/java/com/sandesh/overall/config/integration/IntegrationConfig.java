package com.sandesh.overall.config.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.core.GenericSelector;
import org.springframework.integration.core.GenericTransformer;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.PollerFactory;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.transformer.FileToStringTransformer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.io.File;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Configuration
@IntegrationComponentScan
@Import(SecondIntegrationConfig.class)
public class IntegrationConfig {

    // Without channel also we can directly use string name
    // Eg: if atob message channel is not present then also we can use string name channel("channelName")
    // Its good practice to create channel explicitly

    // @Bean
    public ApplicationRunner runner(IntegrationFlowContext context) {
        return (args) -> {
            IntegrationFlow sourceFlow = sourceFlow(1L);
            IntegrationFlow destinationFlow = destinationFlow();
            Set.of(destinationFlow, sourceFlow).forEach(flow -> context.registration(flow).register().start());
        };
    }

    // @Bean
    public ApplicationRunner greetingRunner(GreetingGateway greetingGateway) {
        return (args) -> {
            for (int i = 0; i < 10; i++) {
                greetingGateway.greet("Hello " + ThreadLocalRandom.current().nextInt(100, 999));
            }
        };
    }

    // @Bean
    public ApplicationRunner publishName(GreetingGateway greetingGateway) {
        return (args) -> {
            String upperCasedName = greetingGateway.publish("Tom Cruise");
            System.out.println("Upper cased name ::: " + upperCasedName);
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

    @Bean
    public MessageChannel publish() {
        return MessageChannels.direct().getObject();
    }

    @Bean
    public MessageChannel receive() {
        return MessageChannels.direct().getObject();
    }

    @Bean
    public MessageChannel greetReply() {
        return MessageChannels.direct().getObject();
    }

    @Bean
    public MessageChannel fileInpChannel() {
        return MessageChannels.direct().getObject();
    }

    @Bean
    public MessageChannel fileOutChannel() {
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

    // @Bean
    public IntegrationFlow gatewayFlowGreet() {
        return IntegrationFlow
                .from(greet())
                .handle((GenericHandler<String>) (payload, headers) -> {
                    System.out.println("Received from Gateway request ::: " + payload);
                    return null;
                }).get();
    }

    // @Bean
    public IntegrationFlow publishFlow() {
        return IntegrationFlow
                .from(publish())
                .transform(String.class, String::toUpperCase)
                .handle((GenericHandler<String>) (payload, headers) -> {
                    System.out.println("Name after transforming ::: " + payload);
                    return payload;
                })
                .channel(receive()).get();
    }

    // @Bean
    public IntegrationFlow gatewayFlowGreetReply() {
        return IntegrationFlow
                .from(greetReply())
                .handle((GenericHandler<String>) (payload, headers) -> {
                    System.out.println("Received from Gateway reply ::: " + payload);
                    return null;
                }).get();
    }

    // @Bean
    public IntegrationFlow fileInputFlow(@Value("${inbound.file.location}") String path) {
        File file = new File(path);
        return IntegrationFlow
                .from(Files.inboundAdapter(file).autoCreateDirectory(true))
                .transform(new FileToStringTransformer())
                .handle((GenericHandler<String>) (payload, headers) -> {
                    System.out.println("Payload: " + payload);
                    System.out.println("Headers: " + headers.toString());
                    return payload;
                })
                .channel(fileInpChannel()).get();
    }

    // @Bean
    public IntegrationFlow listenInputFlow() {
        return IntegrationFlow
                .from(fileInpChannel())
                .filter((GenericSelector<String>) source -> source.startsWith("hello"))
                .transform((GenericTransformer<String, String>) String::toUpperCase)
                .handle((GenericHandler<String>) (payload, headers) -> {
                    System.out.println("Handler in ::: " + payload);
                    return payload;
                })
                .channel(fileOutChannel()).get();
    }

    // @Bean
    public IntegrationFlow listenOutputFlow(@Value("${outbound.file.location}") String path) {
        File file = new File(path);
        return IntegrationFlow
                .from(fileOutChannel())
                .handle((GenericHandler<String>) (payload, headers) -> {
                    System.out.println("Handler out ::: " + payload);
                    return payload;
                })
                .handle(Files.outboundAdapter(file).autoCreateDirectory(true))
                .channel(fileOutChannel()).get();
    }
}
