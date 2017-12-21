package org.inaetics.dronessimulator.pubsub.rabbitmq.subscriber;

import com.rabbitmq.client.ConnectionFactory;
import lombok.Getter;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.Settings;
import org.inaetics.dronessimulator.common.protocol.CompressedProtocolMessage;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.Topic;
import org.inaetics.dronessimulator.pubsub.api.serializer.Serializer;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;
import org.inaetics.dronessimulator.pubsub.rabbitmq.common.RabbitConnection;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A RabbitMQ implementation of a subscriber.
 */
public class RabbitSubscriber extends RabbitConnection implements Subscriber {
    private static final Logger logger = Logger.getLogger(RabbitSubscriber.class);

    /** The identifier of this subscriber. */
    @Getter
    private String identifier;

    /** The handlers for each message class this subscriber processes. */
    private static final Map<Class<? extends Message>, Collection<MessageHandler<Message>>> handlers = new HashMap<>();

    /** The topics this subscriber is subscribed to. */
    private Map<Topic, String> topics;

    /** The consumer that is in use. */
    private RabbitMessageConsumer consumer;

    /**
     * Instantiates a new RabbitMQ subscriber for the given topic.
     * @param connectionFactory The RabbitMQ connection factory to use when starting a new connection.
     * @param identifier The identifier for this subscriber. This is used as queue name.
     * @param serializer The serializer to use.
     */
    public RabbitSubscriber(ConnectionFactory connectionFactory, String identifier, Serializer serializer, Discoverer discoverer) {
        super(connectionFactory, serializer, discoverer);
        this.construct(identifier);
    }

    /**
     * Instantiates a new RabbitMQ subscriber for use with OSGi. This constructor assumes that the serializer will be
     * injected later on.
     * @param connectionFactory The RabbitMQ connection factory to use when starting a new connection.
     * @param identifier The identifier for this subscriber. This is used as queue name.
     */
    @SuppressWarnings("unused") //Suppress unused since it will be used by OSGi
    public RabbitSubscriber(ConnectionFactory connectionFactory, String identifier) {
        super(connectionFactory);
        this.construct(identifier);
    }

    /**
     * Instantiates a new RabbitMQ subscriber for use with OSGi. This constructor assumes that the serializer will be
     * injected later on and the connection factory will be built from a discoverable config.
     * @param identifier  The identifier for this subscriber. This is used as queue name.
     */
    @SuppressWarnings("unused") //Suppress unused since it will be used by OSGi
    public RabbitSubscriber(String identifier) {
        super();
        this.construct(identifier);
    }

    /**
     * Instantiates a new RabbitMQ subscriber for use with OSGi. This constructor assumes that the serializer will be
     * injected later on and the connection factory will be built from a discoverable config. The identifier will be
     * set to a generated UUID.
     */
    @SuppressWarnings("unused") //Suppress unused since it will be used by OSGi
    public RabbitSubscriber() {
        this(UUID.randomUUID().toString());
    }

    /**
     * Performs some initialization for the constructors.
     */
    private void construct(String identifier) {
        assert identifier != null;

        this.identifier = identifier;
        this.topics = new HashMap<>();

        logger.debug("Initialized RabbitMQ subscriber with identifier {}", identifier);
    }

    @Override
    public void addTopic(Topic topic) throws IOException {
        // Add topic to list if not present already
        if (!this.topics.containsKey(topic)) {
            this.topics.put(topic, topic.getName());
            logger.debug("Topic {} added", topic.getName());
        }

        // If connected, restart
        if (this.isConnected()) {
            this.declareTopic(topic);
            this.channel.queueBind(this.identifier, topic.getName(), "");
            logger.debug("RabbitMQ queue {} bound to exchange {}", this.identifier, topic.getName());
            this.updateConsumer();
        }
    }

    @Override
    public void removeTopic(Topic topic) throws IOException {
        // Do nothing if we did not subscribe to this topic
        if (this.topics.containsKey(topic)) {
            // Remove from list
            this.topics.remove(topic);
            logger.debug("Topic {} removed", topic.getName());

            // Unbind if connected
            if (this.isConnected()) {
                this.channel.queueUnbind(this.identifier, topic.getName(), "");
                logger.debug("RabbitMQ queue {} unbound from exchange {}", this.identifier, topic.getName());
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
        // Create new set for this message class if needed
        Collection<MessageHandler<Message>> handlers = RabbitSubscriber.handlers.computeIfAbsent(messageClass, k -> new HashSet<>());
        handlers.add(handler);
        logger.debug("Handler {} set for message class {}", handler, messageClass);
    }

    @Override
    public void addHandlerIfNotExists(Class<? extends Message> messageClass, MessageHandler handler) {
        // Create new set for this message class if needed
        Collection<MessageHandler<Message>> handlers = RabbitSubscriber.handlers.computeIfAbsent(messageClass, k -> new HashSet<>());
        if (handlers.stream().filter(h -> h.getClass().equals(handler.getClass())).count() == 0) {
            handlers.add(handler);
            logger.debug("Handler {} set for message class {}", handler, messageClass);
        }
    }

    /**
     * Removes the given handler for the given message class.
     * @param messageClass The message class to remove the handler for.
     * @param handler The handler to remove.
     */
    @Override
    public void removeHandler(Class<? extends Message> messageClass, MessageHandler handler) {
        Collection<MessageHandler<Message>> handlers = RabbitSubscriber.handlers.get(messageClass);

        // Remove the handler for the class if any handler set is defined
        if (handlers != null) {
            handlers.remove(handler);
            logger.debug("Handler {} removed for message class {}", handler, messageClass);
        }
    }

    /**
     * Processes the received message by passing the message to all registered handlers for the class of the given
     * message.
     * @param message The received message.
     */
    @Override
    public void receive(Message message) {
        logger.debug("Message {} received by queue {}", message.toString(), this.identifier);

        // check if compressed message, then receive recursively
        if (message.getClass().equals(CompressedProtocolMessage.class)) {
            ((CompressedProtocolMessage) message).stream().forEach(this::receive);
            return;
        }

        // apparently not a compressed message, lets continue
        Collection<MessageHandler<Message>> handlers = RabbitSubscriber.handlers.get(message.getClass());

        // Pass the message to every defined handler
        if (handlers != null) {
            for (MessageHandler<Message> handler : handlers) {
                handler.handleMessage(message);
            }
        } else {
            Collection<String> messageTypes = RabbitSubscriber.handlers.keySet().stream().map(Class::toString).collect(Collectors.toSet());
            logger.warn("Message {} was received but is unroutable", message.toString());
            if (logger.isDebugEnabled() && messageTypes.size() == 0) {
                messageTypes.add("no message types found that can be handled. There are " + RabbitSubscriber.handlers.size() + " handlers available in total.");
                logger.debug("Handlers available for messages types: " + String.join(",", messageTypes));
            }
        }
    }

    @Override
    public boolean hasConnection() {
        return this.isConnected();
    }

    @Override
    public void connect() throws IOException {
        super.connect();

        // Define queue
        Map<String, Object> args = new HashMap<>();
//        args.put("x-message-ttl", Settings.TICK_TIME);
        this.channel.queueDeclare(this.identifier, false, false, true, args);
        logger.debug("RabbitMQ queue {} declared", this.identifier);

        // Bind exchanges to queue
        for (String topicName : this.topics.values()) {
            this.channel.queueBind(this.identifier, topicName, "");
            logger.debug("RabbitMQ queue {} bound to exchange {}", this.identifier, topicName);
        }

        this.updateConsumer();
    }

    public void updateConsumer() throws IOException {
        logger.debug("RabbitMQ consumer update requested");
        RabbitMessageConsumer old = this.consumer;

        // Start new consumer
        this.consumer = new RabbitMessageConsumer(this);
        this.channel.basicConsume(this.identifier, true, this.consumer);
        logger.debug("New RabbitMQ consumer started");

        // Cancel old consumer if we have one
        if (old != null && old.getConsumerTag() != null) {
            logger.debug("Cancel old RabbitMQ consumer with tag: " + old.getConsumerTag());
            this.channel.basicCancel(old.getConsumerTag());
            logger.debug("Old RabbitMQ consumer cancelled");
        }
        logger.debug("Consumer update requested");
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
