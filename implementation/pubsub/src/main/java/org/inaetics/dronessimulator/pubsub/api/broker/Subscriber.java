package org.inaetics.dronessimulator.pubsub.api.broker;

import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;

/**
 * Interface for a subscriber.
 */
public interface Subscriber {
    /**
     * Returns the topic this subscriber is interested in.
     * @return The topic of this subscriber.
     */
    Topic getTopic();

    /**
     * Adds a message handler to this subscriber. The message class specifies which kind of message this handler should
     * process.
     * @param messageClass The message class the handler is for.
     * @param handler The handler to process the messages.
     */
    void addHandler(Class<? extends Message> messageClass, MessageHandler handler);

    /**
     * Removes a message handler from this subscriber.
     * @param messageClass The message class to remove the handler for.
     * @param handler The handler to remove.
     */
    void removeHandler(Class<? extends Message> messageClass, MessageHandler handler);

    /**
     * Processes a received message. Processing of messages is relayed to the handlers.
     * @param message The received message.
     */
    void receive(Message message);
}
