# Apache Kafka

Apache Kafka is a platform that

- Publish and subscribe to streams of records, similar to a message queue or enterprise messaging system.
- Stores streams of records in a fault-tolerant *durable* way - they are not removed, unlike the usual message queue.

(from the [Introduction](https://kafka.apache.org/intro))

This makes Apache Kafka suited as a backend for [Event sourcing](http://martinfowler.com/eaaDev/EventSourcing.html), a style of application design where state changes are logged as a time-ordered sequence of records. 

Apache Kafka is also used for Stream processing, where data in processing pipelines consisting of multiple stages, where raw input data is consumed from Kafka topics and then aggregated, enriched, or otherwise transformed into new topics for further consumption or follow-up processing. For example, a processing pipeline for recommending news articles might crawl article content from RSS feeds and publish it to an "articles" topic; further processing might normalize or deduplicate this content and publish the cleansed article content to a new topic; a final processing stage might attempt to recommend this content to users.

(from the [Use cases](https://kafka.apache.org/uses))

## Command-line tools for inspecting/troubleshooting

Kafka comes with a couple of command-line tools that can be used to interact with the cluster.

This is not an exhaustive reference to the tools; there are many more options, run the commands with no arguments to view help on using it.

The examples below assume access to Kafka command-line tools. If you are working with a Kafka deployed on Kubernetes (assuming Kafka is deployed in the `kafka` namespace and a  `testclient` pod is available), you can access these tools via:

```console
$ kubectl exec testclient -n kafka -it /bin/bash
```

### View messages in a topic

If we would like to see all messages in a topic, we can use the Kafka client command line tool:

```console
$ kafka-console-consumer \
--bootstrap-server kafka.kafka.svc.cluster.local:9092 \
--topic <enter topic name> \
--offset earliest \
--partition 0
```

If you would also like to view the key, pass the following options to print the key, separated from the message with a Tab character†.

```console
... --property print.key=true --property key.separator="[press Tab key]" ...
```

†On a client machine (Ubuntu), we had to press <kbd>Ctrl</kbd> <kbd>V</kbd> on the keyboard followed by the <kbd>Tab</kbd> key to enter a literal Tab character. (The <kbd>Ctrl</kbd> <kbd>V</kbd> means to "verbatimly insert the next character pressed" https://superuser.com/a/421468.)

### Checking if a consumer is lagging on a topic

A consumer is *lagging* on a topic if the offset is less than the number of messages present in the topic.

We can view this by running `kafka-consumer-groups`:

```console
$ kafka-consumer-groups \
--bootstrap-server kafka.kafka.svc.cluster.local:9092 \
--group <consumer group name> \
--describe
```

If you don't know the consumer group, you can get all of them with `--list`:

```console
$ kafka-consumer-groups \
--bootstrap-server kafka.kafka.svc.cluster.local:9092 \
--list
```

### Resetting the offset for a consumer

TBD

## Annotated Bibliography

1. [Official Apache Kafka documentation](https://kafka.apache.org/documentation)

   The Getting Started section of the Documentation has definition for terms and concepts. There are some diagrams. But the definitions are a little terse, and might not be sufficient when thinking about more complex scenarios, such as for consumer groups, how will partitions get assigned to consumers if there are more consumers than partitions? 

   The Design section of the Documentation gives the motivation of certain Kafka constructs, which was useful to have a deeper understanding of Kafka. For example, [the discussion on Consumer Position](https://kafka.apache.org/documentation/#design_consumerposition), from the perspective of messaging systems, was helpful in understanding the concept of consumer groups (and why partitions gets assigned to consumers).

   You can safely skip [the Introduction page](https://kafka.apache.org/intro) from the Apache Kafka site, because it duplicates the Getting Started section of the Documentation.

1. [Kafka: The Definitive Guide (2017)](https://learning.oreilly.com/library/view/kafka-the-definitive/9781491936153/) (paid)

   After giving definitions for concepts and terms, the book is organised by usage (eg. producing records, consuming records), and accompanies the usage with recommendations and examples. Plenty of diagrams and explores how the concepts link together to give you a deeper understanding of Kafka. For example, [the section on Consumers and Consumer Groups](https://learning.oreilly.com/library/view/kafka-the-definitive/9781491936153/ch04.html#idm45788273657144) was helpful in understanding how partitions are assigned 1-1 to a consumer in a consumer group.

1. [Javadocs](https://kafka.apache.org/documentation/#consumerapi)

   (Unfortunately there is no symlink to the latest current/stable version, so follow the "javadocs" link in the general Apache Kafka documentation linked.)

   It is useful to read client API documentation such as the Consumer API (linked above), because some responsibilities are performed at the client-side instead of on the Kafka broker. For example, the Consumer Groups and Topic Subscriptions section details how you would prefer the `subscribe` API over the `assign` API.
