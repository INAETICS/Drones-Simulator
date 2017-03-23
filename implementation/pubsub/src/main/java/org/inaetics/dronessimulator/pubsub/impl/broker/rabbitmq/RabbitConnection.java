package org.inaetics.dronessimulator.pubsub.impl.broker.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.inaetics.dronessimulator.pubsub.api.serializer.Serializer;
import org.inaetics.dronessimulator.pubsub.api.Topic;

import java.io.IOException;
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
    /** The RabbitMQ connection used by this subscriber. */
    protected Connection connection;

    /** The connection channel to use, created from the connection. */
    protected Channel channel;

    /** The serializer used in this connection. */
    protected Serializer serializer;

    /** Collection of topics for which an exchange has been declared. */
    private Collection<Topic> declaredTopics;

    /**
     * Sets up the connection for use.
     * @param connection The RabbitMQ connection to use.
     * @param serializer The serializer to use.
     */
    protected RabbitConnection(Connection connection, Serializer serializer) {
        this(serializer);
        assert connection != null;
        this.connection = connection;
    }

    /**
     * Sets up the connection for testing. Since no actual connection will be present when using this constructor
     * directly it can only be used for testing purposes.
     * @param serializer The serializer to use.
     */
    protected RabbitConnection(Serializer serializer) {
        assert serializer != null;
        this.serializer = serializer;
        this.declaredTopics = new HashSet<>();
    }

    /**
     * Make a connection to the RabbitMQ broker and sets up the exchange used by this connection.
     * @throws IOException Error while setting up the connection.
     */
    public void connect() throws IOException {
        // Create a channel if not present
        if (!isConnected()) {
            channel = connection.createChannel();

            // Clear declared topics cache
            declaredTopics.clear();
        }
    }

    /**
     * Disconnects from the RabbitMQ broker.
     * @throws IOException Error while gracefully closing the connection.
     * @throws TimeoutException The connection timed out.
     */
    public void disconnect() throws IOException, TimeoutException {
        // Close the channel if necessary
        if (isConnected()) {
            channel.close();
        }
    }

    /**
     * Returns whether the connection with the RabbitMQ broker is usable.
     * @return The connection status.
     */
    public boolean isConnected() {
        return channel != null && channel.isOpen();
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
}
