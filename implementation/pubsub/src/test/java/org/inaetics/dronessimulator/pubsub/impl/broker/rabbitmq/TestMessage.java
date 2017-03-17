package org.inaetics.dronessimulator.pubsub.impl.broker.rabbitmq;

import org.inaetics.dronessimulator.pubsub.api.Message;

/**
 * A message used for testing the RabbitMQ implementation.
 */
public class TestMessage implements Message {
    private String message;

    public TestMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
