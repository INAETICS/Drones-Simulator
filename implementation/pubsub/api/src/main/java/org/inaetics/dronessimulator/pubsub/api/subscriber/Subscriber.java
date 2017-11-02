package org.inaetics.dronessimulator.pubsub.api.subscriber;

import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.protocol.Message;
import org.inaetics.dronessimulator.pubsub.protocol.Topic;

import java.io.IOException;

/**
 * Interface for a subscriber.
 */
public interface Subscriber {
    /**
     * Subscribes to the given topic.
     * @param topic The topic to subscribe to.
     */
    void addTopic(Topic topic) throws IOException;

    /**
     * Unsubscribes from the given topic.
     * @param topic The topic to unsubscribe from.
     */
    void removeTopic(Topic topic) throws IOException;

    /**
     * Adds a message handler to this subscriber. The message class specifies which kind of message this handler should
     * process.
     * @param messageClass The message class the handler is for.
     * @param handler The handler to process the messages.
     */
    void addHandler(Class<? extends Message> messageClass, MessageHandler handler);

    void addHandlerIfNotExists(Class<? extends Message> messageClass, MessageHandler handler);

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

    boolean hasConnection();

    void connect() throws IOException;
}
