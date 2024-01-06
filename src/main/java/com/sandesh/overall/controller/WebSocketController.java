package com.sandesh.overall.controller;

import com.sandesh.overall.util.GenericUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class WebSocketController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String greeting(String message) {
        log.info("Received message: " + message);
        GenericUtil.sleep(1000L);
        return "Received: " + message;
    }
}
