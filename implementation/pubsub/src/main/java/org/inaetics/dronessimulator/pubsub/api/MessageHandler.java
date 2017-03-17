package org.inaetics.dronessimulator.pubsub.api;

/**
 * Interface for a handler for received messages.
 */
public interface MessageHandler {
    /**
     * Processes a received message.
     * @param message The received message.
     */
    void handleMessage(Message message);
}
