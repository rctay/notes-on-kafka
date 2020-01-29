package com.example.demo.components;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DemoListener {

    private String lastMessageReceived;

    public String getLastMessageReceived() {
        return lastMessageReceived;
    }

    @KafkaListener(topics = "topicA", id = "demo-application-2")
    public void listen(String message) {
        log.debug("received mesage: " + message);
        lastMessageReceived = message;
    }
}
