package org.inaetics.dronessimulator.pubsub.api.broker;

import org.inaetics.dronessimulator.pubsub.api.Message;

import java.io.IOException;

/**
 * Interface for a publisher.
 */
public interface Publisher {
    /**
     * Returns the topic messages are published to.
     * @return The topic of this publisher.
     */
    Topic getTopic();

    /**
     * Send a message to the message broker.
     * @param message The message to send.
     */
    void send(Message message) throws IOException;
}
