# Kafka Consumer Demo

This project demonstrates different configuration styles for a Kafka consumer to listen for messages with a string key and JSON value on the topic `topicA` — and tests!

In this project, we use the `@KafkaListener` annotation on bean method style of setting up a consumer. The other styles like annotating the class are left as an exercise to the reader.

## Explicit consumer

The configuration style in [ExplicitConsumerConfig](src/main/java/com/example/demo/configuration/ExplicitConsumerConfig.java) is the same one shown in the Introduction section of the Spring for Kafka reference manual.

This is probably the minimum configuration you need to get a consumer working.

To see the app in action, you can start the app with the active profile `use-explicit-config` and set the property `demo.kafka.bootstrap-servers` to your Kafka broker address.

## Spring Boot

Actually, I lied when I said that was the minimum - you can strip off even more, if you use Spring Boot, as it comes with Kafka auto-configuration included. However, if we were to (like the above) provide `kafkaListenerContainerFactory()` <- `consumerFactory()` <- `consumerConfigs()`, all of which we explicitly constructed, we may find the Spring Boot integration to not work. For example, the `spring.kafka.consumer.*` properties would not have any effect, since you constructed the consumer configuration explicitly in `consumerConfigs()`, and passed those on to the consumer and listener container factory.

The configuration in [BootyConsumerConfig](src/main/java/com/example/demo/configuration/BootyConsumerConfig.java) shows how to configure a consumer without losing the integration that Spring Boot offers out-of-the-box.

Note that we opted to set the deserializer configuration in Java code, even though Spring Boot picks up the properties at `spring.kafka.consumer.*`, because we can leverage autocompleting the package to a deserializer in the IDE. We lack this when editing `application.properties`. For example, to specify to use JSON deserialization with the `spring.kafka.consumer.value-deserializer` property, we would have to type by hand `org`, followed by `.springframework`, and so on to point to `JSONDeserializer`.

To see the app in action, you can start the app with the active profile `use-booty-config` and set the property `spring.kafka.consumer.bootstrap-servers` to your Kafka broker address.

## Testing Kafka Consumers

The Spring for Kafka reference manual dosen't have enough information on setting up an integration test of a consumer we have defined in our application; they only offer an example with the listener and listener container factory explicitly constructed in the test.

To do so is challenging because we need to somehow access the listener containers that are constructed automatically for us by Spring Kafka. One approach is shown in our tests:

```java
// @EmbeddedKafka(...)
// @SpringBootTest
class MyKafkaTest {
    // ...

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

    // ...
}
```

The [Awaitility](https://github.com/awaitility/awaitility) library is also used, which is helpful as it is uncertain how long before a Kafka message gets received in our test code:

```java
await()
        .atMost(Duration.of(10, SECONDS))
        .until(() -> demoListener.getLastMessageReceived() != null);
```

## The `kafkaListenerContainerFactory` name

You might think of giving a different name to the listener container factory instead of `kafkaListenerContainerFactory()`, reasoning that this would work because the type signatures for the key and value are unchanged. The Spring Kafka reference manual section on [the `@KafkaListener` annotation](https://docs.spring.io/spring-kafka/reference/html/#kafka-listener-annotation), as well as the Javadoc for `@KafkaListener`, explains this — the default container factory is assumed to be available with a bean name of `kafkaListenerContainerFactory` unless one was explicitly defined via the `containerFactory` parameter to the annotation.