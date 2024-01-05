package com.sandesh.overall.util;

import com.github.javafaker.Faker;
import com.sandesh.overall.model.Temperature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class ServerSentEventsUtil {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final Faker faker = new Faker();

    @Scheduled(fixedDelay = 2000L)
    public void emitRandomTemps() {
        emitters.forEach(emitter -> {
            try {
                log.info("Generating random temperature..");
                emitter.send(new Temperature(faker.random().nextLong(), faker.job().keySkills()));
            } catch (IOException e) {
                log.error("Some emitter stopped working");
            }
        });
    }

    public void addEmitter(SseEmitter emitter) {
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));
    }
}
