package com.sandesh.overall.config.integration;

import com.github.javafaker.Faker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.core.GenericTransformer;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

public class ErrorIntegrationConfig {

    private static final String CUSTOM_CHANNEL = "customChannel";
    private static final String CUSTOM_ERROR_CHANNEL = "customErrorChannel";

    @Bean(name = CUSTOM_ERROR_CHANNEL)
    public MessageChannel customErrorChannel() {
        return MessageChannels.direct().getObject();
    }

    @Bean(name = CUSTOM_CHANNEL)
    public MessageChannel customChannel() {
        return MessageChannels.direct().getObject();
    }

    // @Bean
    public IntegrationFlow customFlow() {
        Faker faker = new Faker();
        return IntegrationFlow
                .from((MessageSource<String>) () -> MessageBuilder.withPayload(faker.funnyName().name()).build(), poller -> poller.poller(pm -> pm.fixedRate(3_000)))
                .enrichHeaders(spec -> spec.errorChannel(CUSTOM_ERROR_CHANNEL))
                .handle((GenericHandler<String>) (payload, headers) -> {
                    System.out.println("Handler name is ::: " + payload);
                    if (StringUtils.containsAny(payload, "j", "J"))
                        throw new IllegalArgumentException("Name contains ignore case letter j");
                    return payload;
                })
                .transform((GenericTransformer<String, String>) String::toUpperCase)
                .channel(CUSTOM_CHANNEL)
                .get();

    }

    // @Bean
    public IntegrationFlow customOutputFlow() {
        return IntegrationFlow
                .from(CUSTOM_CHANNEL)
                .handle((GenericHandler<String>) (payload, headers) -> {
                    System.out.println("Valid flow Name ::: " + payload);
                    return null;
                }).get();
    }

    // @Bean
    public IntegrationFlow errorFlow() {
        return IntegrationFlow
                .from(CUSTOM_ERROR_CHANNEL)
                .handle((payload, headers) -> {
                    System.out.println("Inside error flow Name ::: " + payload);
                    return null;
                }).get();
    }
}
