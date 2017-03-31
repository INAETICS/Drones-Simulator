package org.inaetics.dronessimulator.pubsub.rabbitmq.subscriber;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import org.inaetics.dronessimulator.pubsub.api.*;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;
import org.inaetics.dronessimulator.pubsub.api.Topic;
import org.inaetics.dronessimulator.pubsub.api.serializer.Serializer;
import org.inaetics.dronessimulator.pubsub.rabbitmq.common.RabbitConnection;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * A RabbitMQ implementation of a subscriber.
 */
public class RabbitSubscriber extends RabbitConnection implements Subscriber {
    /** The identifier of this subscriber. */
    private String identifier;

    /** The handlers for each message class this subscriber processes. */
    private Map<Class<? extends Message>, Collection<MessageHandler>> handlers;

    /** The topics this subscriber is subscribed to. */
    private Map<Topic, String> topics;

    /** The listener thread. */
    private Thread listenerThread;

    /**
     * Instantiates a new RabbitMQ subscriber for the given topic.
     * @param connection The RabbitMQ connection to use.
     * @param identifier The identifier for this subscriber. This is used as queue name.
     * @param serializer The serializer to use.
     */
    public RabbitSubscriber(Connection connection, String identifier, Serializer serializer) {
        super(connection, serializer);
        this.construct(identifier);
    }

    /**
     * Instantiates a new RabbitMQ subscriber for use with OSGi. This constructor assumes that the serializer will be
     * injected later on.
     * @param connection The RabbitMQ connection to use.
     * @param identifier The identifier for this subscriber. This is used as queue name.
     */
    public RabbitSubscriber(Connection connection, String identifier) {
        super(connection);
        this.construct(identifier);
    }

    /**
     * Performs some initialization for the constructors.
     */
    private void construct(String identifier) {
        assert identifier != null;

        this.identifier = identifier;
        this.handlers = new HashMap<>();
        this.topics = new HashMap<>();
    }

    @Override
    public void addTopic(Topic topic) throws IOException {
        // Add topic to list if not present already
        if (!this.topics.containsKey(topic)) {
            this.topics.put(topic, topic.getName());
        }

        // Make sure exchange exists
        this.declareTopic(topic);

        // If connected, bind to queue
        if (this.isConnected()) {
            this.channel.queueBind(this.identifier, this.topics.get(topic), "");
        }
    }

    @Override
    public void removeTopic(Topic topic) throws IOException {
        // Do nothing if we did not subscribe to this topic
        if (this.topics.containsKey(topic)) {
            // Remove from list
            this.topics.remove(topic);

            // Unbind if connected
            if (this.isConnected()) {
                this.channel.queueUnbind(this.identifier, topic.getName(), "");
            }
        }
    }

    /**
     * Adds a message handler for a given message class.
     *
     * Multiple handlers for the same message class are supported, in which case the messages are passed to all handlers
     * for the relevant message type.
     *
     * @param messageClass The message class the handler is for.
     * @param handler The handler to process the messages.
     */
    @Override
    public void addHandler(Class<? extends Message> messageClass, MessageHandler handler) {
        Collection<MessageHandler> handlers = this.handlers.get(messageClass);

        if (handlers == null) {
            // Create new set for this message class if needed
            handlers = new HashSet<>();
            this.handlers.put(messageClass, handlers);
        }

        handlers.add(handler);
    }

    /**
     * Removes the given handler for the given message class.
     * @param messageClass The message class to remove the handler for.
     * @param handler The handler to remove.
     */
    @Override
    public void removeHandler(Class<? extends Message> messageClass, MessageHandler handler) {
        Collection<MessageHandler> handlers = this.handlers.get(messageClass);

        // Remove the handler for the class if any handler set is defined
        if (handlers != null) {
            handlers.remove(handler);
        }
    }

    /**
     * Processes the received message by passing the message to all registered handlers for the class of the given
     * message.
     * @param message The received message.
     */
    @Override
    public void receive(Message message) {
        Collection<MessageHandler> handlers = this.handlers.get(message.getClass());

        // Pass the message to every defined handler
        if (handlers != null) {
            for (MessageHandler handler : handlers) {
                handler.handleMessage(message);
            }
        }
    }

    @Override
    public void connect() throws IOException {
        super.connect();

        // Define queue
        this.channel.queueDeclare(this.identifier, false, false, true, null);

        // Bind exchanges to queue
        for (String topicName : this.topics.values()) {
            this.channel.queueBind(this.identifier, topicName, "");
        }

        // Actually listen for messages
        RabbitMessageConsumer consumer = new RabbitMessageConsumer(this);
        this.listenerThread = new Thread(consumer);
        this.listenerThread.start();
    }

    @Override
    public void disconnect() throws IOException, TimeoutException {
        // Stop listener thread
        if (this.listenerThread != null) {
            this.listenerThread.interrupt();
        }

        super.disconnect();
    }

    @Override
    public boolean isConnected() {
        return super.isConnected() && this.listenerThread != null && this.listenerThread.isAlive();
    }

    public String getIdentifier() {
        return identifier;
    }
}
