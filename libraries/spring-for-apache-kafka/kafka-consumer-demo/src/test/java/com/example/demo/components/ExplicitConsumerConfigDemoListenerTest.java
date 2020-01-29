package com.example.demo.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Map;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@ActiveProfiles("use-explicit-config")
@EmbeddedKafka(bootstrapServersProperty = "demo.kafka.bootstrap-servers", controlledShutdown = true)
class ExplicitConsumerConfigDemoListenerTest {

    @Autowired
    private DemoListener demoListener;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @BeforeEach
    void setUp() {
        // source: https://codenotfound.com/spring-kafka-embedded-unit-test-example.html#6-testing-the-consumer
        for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(messageListenerContainer, embeddedKafka.getPartitionsPerTopic());
        }
    }

    @Test
    void listener_whenRecordIsSentOnTopicA_receivesMessage() {
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafka);
        ProducerFactory<String, String> pf = new DefaultKafkaProducerFactory<>(producerProps);
        KafkaTemplate<String, String> template = new KafkaTemplate<>(pf);
        template.setDefaultTopic("topicA");
        template.sendDefault("\"foo\"");

        await()
                .atMost(Duration.of(10, SECONDS))
                .until(() -> demoListener.getLastMessageReceived() != null);

        assertThat(demoListener.getLastMessageReceived()).isEqualTo("foo");
    }
}
