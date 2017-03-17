package org.inaetics.dronessimulator.pubsub.impl.broker.rabbitmq;

import org.inaetics.dronessimulator.pubsub.api.Message;

/**
 * A message used for testing the RabbitMQ implementation.
 */
public class TestIntMessage implements Message {
    private int message;

    public TestIntMessage(int message) {
        this.message = message;
    }

    public int getMessage() {
        return message;
    }
}
