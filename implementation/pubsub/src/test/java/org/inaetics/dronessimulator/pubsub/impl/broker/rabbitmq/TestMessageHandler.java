package org.inaetics.dronessimulator.pubsub.impl.broker.rabbitmq;

import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;

/**
 * A message handler used for testing the RabbitMQ subscriber implementation.
 */
public class TestMessageHandler implements MessageHandler {
    private TestMessage message;

    public TestMessageHandler() {
        this.message = null;
    }

    public void handleMessage(Message message) {
        try {
            this.message = (TestMessage) message;
        } catch (ClassCastException ignore) {
            // Do nothing
        }
    }

    public TestMessage getMessage() {
        return message;
    }
}
