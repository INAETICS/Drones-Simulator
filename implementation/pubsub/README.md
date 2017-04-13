# Pubsub

This module contains a publisher/subscriber API and implementations.
This document describes the public API and shows how a module might use it.
Each submodule contains its own documentation when appropriate.

## General concepts

This library uses the following concepts:

- **Message**: Data that is exchanged between a publisher and a subscriber.
- **Topic**: Category of a message. Publishers can send data to topics, subscribers can subscribe to them.
- **Publisher**: Program that builds a message to share it.
- **Subscriber**: Program that listens for messages.
- **Handler**: Partial program that processes a message (on the side of the subscriber).

The publisher and subscriber are, together with a message serializer, implemented for you.
You might have different choices, it is therefore mandatory to use the same serializer at each end and use corresponding publishers and subscribers.

## Defining messages and topics

It is important that both the publisher and subscriber programs use the same topics and messages (and can load these), otherwise messages can not be processed.

### Defining message types

A message type can be seen as a message template - it allows for containing certain data.

Each message type should be a Java class implementing the `Message` interface.
The message interface extends from Java's builtin `Serializable` interface, making each individual message serializable.

An example of a very simple message is shown below.

```java
package mymessages;

import org.inaetics.dronessimulator.pubsub.api.Message;

class SimpleMessage implements Message {
    private String message;
    
    public Message(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return this.message;
    }
}
```

Keep in mind that certain data structures might come with a large serialization overhead (either time or size) and are therefore not recommended.

### Defining topics

Topics are basically strings.
Therefore the `Topic` interface provides a `getName` method which is used to get the actual topic name that is used by the message broker.
You are free to setup your own naming scheme, perhaps accepting certain objects from your program in the constructor of your topic.
An example is given below.

```java
package mytopics;

import org.inaetics.dronessimulator.pubsub.api.Topic;

class NumberedTopic implements Topic {
    public static final String BASE_NAME = "Topic";
    
    private int number;
    
    public NumberedTopic(int number) {
        this.number = number;
    }
    
    public String getName() {
        return String.format("%s#%d", BASE_NAME, number);
    }
}
```

## Sending and receiving messages

To send and receive messages you must be running a serializer, publisher and subscriber in Apache Felix.
We assume your bundle depends on these bundles and you have access to the implemented interfaces.

### Preparing your subscriber

To be able to receive messages, you must tell the subscriber which topics you are interested in.
For this, subscribers implement the `addTopic` interface (see the Javadoc for details).

To be able to process messages you have to define a message handler.
Message handlers are Java classes implementing the `MessageHandler` interface.
A simple example, which just prints that it received something, is shown below.

```java
package myhandlers;

import org.inaetics.dronessimulator.pubsub.api.MessageHandler;

class TestHandler implements MessageHandler {
    public synchronized void handleMessage(Message message) {
        System.out.println("Message received!");
    }
}
```

To add your handler to the subscriber, use the `addHandler` method.
It is important that you specify the concrete message classes instead of an abstract class since only the exact class is matched, no base classes are taken into account.

### Sending a message

To send a message, call `send` on the publisher together with a topic and a message.
Refer to the Javadoc for details.
