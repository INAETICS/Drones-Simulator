package org.inaetics.dronessimulator.pubsub.javaserializer;

import org.inaetics.dronessimulator.pubsub.api.Message;

/**
 * Message implementation to use for testing the Java serializer.
 */
public class TestMessage implements Message {
    private final transient String notSerialized;
    private String message;

    public TestMessage(String message) {
        this.message = message;
        this.notSerialized = new StringBuilder(message).reverse().toString();
    }

    public String getNotSerialized() {
        return notSerialized;
    }

    public String getMessage() {
        return message;
    }
}
