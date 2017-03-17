package org.inaetics.dronessimulator.pubsub.impl.broker.rabbitmq;

import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;

/**
 * A message handler used for testing the RabbitMQ subscriber implementation.
 */
public class TestIntMessageHandler implements MessageHandler {
    private TestIntMessage message;

    public TestIntMessageHandler() {
        this.message = null;
    }

    public void handleMessage(Message message) {
        try {
            this.message = (TestIntMessage) message;
        } catch (ClassCastException ignore) {
            // Do nothing
        }
    }

    public TestIntMessage getMessage() {
        return message;
    }
}
