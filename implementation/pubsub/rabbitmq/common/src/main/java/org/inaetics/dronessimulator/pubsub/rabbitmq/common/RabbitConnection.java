package org.inaetics.dronessimulator.pubsub.rabbitmq.common;

import com.rabbitmq.client.*;
import org.inaetics.dronessimulator.pubsub.api.serializer.Serializer;
import org.inaetics.dronessimulator.pubsub.api.Topic;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.TimeoutException;

/**
 * A wrapper for RabbitMQ connections.
 *
 * This abstract class contains functionality which is shared between the RabbitMQ publisher and subscriber classes
 * related to the connection to the broker.
 */
public abstract class RabbitConnection {
    /** The connection factory used for setting up a connection. */
    private ConnectionFactory connectionFactory;

    /** The RabbitMQ connection used by this subscriber. */
    private Connection connection;

    /** The connection channel to use, created from the connection. */
    protected Channel channel;

    /** The serializer used in this connection. */
    protected volatile Serializer serializer;

    /** Collection of topics for which an exchange has been declared. */
    private Collection<Topic> declaredTopics;

    /**
     * Sets up the connection for use.
     * @param connectionFactory The RabbitMQ connection factory to use when starting a new connection.
     * @param serializer The serializer to use.
     */
    protected RabbitConnection(ConnectionFactory connectionFactory, Serializer serializer) {
        this(connectionFactory);
        assert serializer != null;
        this.serializer = serializer;
    }

    /**
     * Sets up the connection for use within OSGi. This constructor assumes that the serializer is injected later on.
     * @param connectionFactory The RabbitMQ connection factory to use when starting a new connection.
     */
    protected RabbitConnection(ConnectionFactory connectionFactory) {
        assert connectionFactory != null;
        this.connectionFactory = connectionFactory;
        this.declaredTopics = new HashSet<>();
    }

    /**
     * Make a connection to the RabbitMQ broker and sets up the exchange used by this connection.
     * @throws IOException Error while setting up the connection.
     */
    public void connect() throws IOException {
        // Create a channel if not present
        if (!isConnected()) {
            try {
                connection = connectionFactory.newConnection();
            } catch (ConnectException | TimeoutException e) {
                e.printStackTrace();
                throw new IOException(e);
            }

            channel = connection.createChannel();

            declaredTopics.clear();
        }
    }

    /**
     * Disconnects from the RabbitMQ broker.
     */
    public void disconnect() throws IOException {
        try {
            // Close the channel if necessary
            if (isConnected()) {
                channel.close();
            }

            connection.close();
        } catch (AlreadyClosedException ignored) {
            // Good! We are already done.
        } catch (TimeoutException e) {
            throw new IOException(e);
        }
    }

    /**
     * Reconnects to the RabbitMQ broker.
     *
     * Calls disconnect and connect in this order.
     */
    public void reconnect() throws IOException {
        // First disconnect, then connect
        this.disconnect();
        this.connect();
    }

    /**
     * Returns whether the connection with the RabbitMQ broker is usable.
     * @return The connection status.
     */
    public boolean isConnected() {
        return connection != null && channel != null && channel.isOpen();
    }

    /**
     * Declares the topic as RabbitMQ exchange using the default settings.
     * @param topic The topic to declare.
     */
    protected void declareTopic(Topic topic) throws IOException {
        // Automatically (re)connect if needed
        if (!isConnected()) {
            this.connect();
        }

        if (!declaredTopics.contains(topic)) {
            channel.exchangeDeclare(topic.getName(), BuiltinExchangeType.FANOUT, false);
            declaredTopics.add(topic);
        }
    }

    /**
     * Returns the AMQP channel used by this connection. May be null if not connected.
     * @return The used AMQP channel.
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Returns the serializer used by this connection. May be null if unset.
     * @return The used serializer.
     */
    public Serializer getSerializer() {
        return serializer;
    }
}
