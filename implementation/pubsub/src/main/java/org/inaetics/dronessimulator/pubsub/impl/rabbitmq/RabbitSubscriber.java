package org.inaetics.dronessimulator.pubsub.impl.rabbitmq;

import com.rabbitmq.client.Connection;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.Subscriber;
import org.inaetics.dronessimulator.pubsub.api.Topic;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * A RabbitMQ implementation of a subscriber.
 */
public class RabbitSubscriber extends RabbitConnection implements Subscriber { // TODO Implement run loop
    /** The identifier of this subscriber. */
    private String identifier;

    /** The name of the RabbitMQ queue this subscriber uses. */
    private String queueName;

    /** The handlers for each message class this subscriber processes. */
    private Map<Class<? extends Message>, Collection<MessageHandler>> handlers;

    /**
     * Instantiates a new RabbitMQ subscriber for the given topic.
     * @param connection The RabbitMQ connection to use.
     * @param topic The topic this subscriber is interested in.
     * @param identifier The identifier for this subscriber. This is used as queue name.
     */
    public RabbitSubscriber(Connection connection, Topic topic, String identifier) {
        super(connection, topic);

        assert identifier != null;

        this.identifier = identifier;
        this.handlers = new HashMap<Class<? extends Message>, Collection<MessageHandler>>();

        this.queueName = String.format("%s/%s", this.topic.getName(), this.identifier);
    }

    /**
     * Connects to the RabbitMQ broker and sets up the exchange and queue used by this subscriber.
     * @throws IOException Error while setting up the connection.
     */
    public void connect() throws IOException {
        super.connect();

        // Declare AMQP queue for the subscriber
        //   durable: no, we do not need the queue after a server restart
        //   exclusive: yes, only we are allowed to read from the queue
        //   autoDelete: yes, this queue is no longer needed when we disconnect
        this.channel.queueDeclare(this.queueName, false, true, true, null);

        // Register the queue to receive messages for the set topic
        this.channel.queueBind(this.queueName, this.exchangeName, "");
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
    public void addHandler(Class<? extends Message> messageClass, MessageHandler handler) {
        Collection<MessageHandler> handlers = this.handlers.get(messageClass);

        if (handlers == null) {
            // Create new set for this message class if needed
            handlers = new HashSet<MessageHandler>();
            this.handlers.put(messageClass, handlers);
        } else {
            // Otherwise set the handlers collection to the existing set
            handlers = this.handlers.get(messageClass);
        }

        handlers.add(handler);
    }

    /**
     * Removes the given handler for the given message class.
     * @param messageClass The message class to remove the handler for.
     * @param handler The handler to remove.
     */
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
    public void receive(Message message) {
        Collection<MessageHandler> handlers = this.handlers.get(message.getClass());

        // Pass the message to every defined handler
        if (handlers != null) {
            for (MessageHandler handler : handlers) {
                handler.handleMessage(message);
            }
        }
    }
}
