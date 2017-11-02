package org.inaetics.dronessimulator.pubsub.api;

import org.inaetics.dronessimulator.pubsub.protocol.Message;

/**
 * Interface for a handler for received messages.
 */
 @FunctionalInterface
public interface MessageHandler {
    /**
     * Processes a received message.
     * @param message The received message.
     */
    void handleMessage(Message message);
}
