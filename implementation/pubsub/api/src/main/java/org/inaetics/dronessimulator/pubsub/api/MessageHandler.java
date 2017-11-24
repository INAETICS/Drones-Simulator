package org.inaetics.dronessimulator.pubsub.api;

/**
 * Interface for a handler for received messages.
 */
 @FunctionalInterface
 public interface MessageHandler<M extends Message> {
    /**
     * Processes a received message.
     * @param message The received message.
     */
    void handleMessage(M message);
}
