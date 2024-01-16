package com.sandesh.overall.config.integration;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

/**
 * Sends message to greet channel if the method greet is called with message parameter
 */
@MessagingGateway
public interface GreetingGateway {

    @Gateway(requestChannel = "greet")
    void greet(String msg);

    // If reply channel is present and the value is used then spring won't throw exception for not listening from the reply channel
    // Otherwise if from receive channel, integration flow is not created then an exception will be thrown
    @Gateway(requestChannel = "publish", replyChannel = "receive")
    String publish(String name);
}
