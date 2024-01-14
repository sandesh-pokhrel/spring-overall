package com.sandesh.overall.config.integration;

import org.springframework.integration.annotation.MessagingGateway;

/**
 * Sends message to greet channel if the method greet is called with message parameter
 */
@MessagingGateway(defaultRequestChannel = "greet")
public interface GreetingGateway {

    void greet(String msg);
}
