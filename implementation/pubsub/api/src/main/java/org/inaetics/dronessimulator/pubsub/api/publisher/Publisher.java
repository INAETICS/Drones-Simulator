package org.inaetics.dronessimulator.pubsub.api.publisher;

import org.inaetics.dronessimulator.pubsub.protocol.Message;
import org.inaetics.dronessimulator.pubsub.protocol.Topic;

import java.io.IOException;

/**
 * Interface for a publisher.
 */
public interface Publisher {
    /**
     * Send a message to the message broker.
     * @param topic The topic to publish this message to.
     * @param message The message to send.
     */
    void send(Topic topic, Message message) throws IOException;
}
