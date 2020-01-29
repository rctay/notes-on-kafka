# Apache Kafka

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
