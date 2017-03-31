package org.inaetics.dronessimulator.pubsub.rabbitmq;

import org.inaetics.dronessimulator.pubsub.api.Message;

/**
 * A message for test purposes.
 */
public class TestMessage implements Message {
    private String message;

    public TestMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TestMessage && this.getMessage().equals(((TestMessage) obj).getMessage());
    }
}
