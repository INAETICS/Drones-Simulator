package org.inaetics.dronessimulator.pubsub.impl.broker.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.inaetics.dronessimulator.pubsub.api.Serializer;
import org.inaetics.dronessimulator.pubsub.api.Topic;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * A wrapper for RabbitMQ connections.
 *
 * This abstract class contains functionality which is shared between the RabbitMQ publisher and subscriber classes
 * related to the connection to the broker.
 */
public abstract class RabbitConnection {
    /** The topic for this connection. */
    protected Topic topic;

    /** The RabbitMQ connection used by this subscriber. */
    protected Connection connection;

    /** The connection channel to use, created from the connection. */
    protected Channel channel;

    /** The name of the exchange this connection uses. */
    protected String exchangeName;

    /** The serializer used in this connection. */
    protected Serializer serializer;

    /**
     * Sets up the connection for use.
     * @param connection The RabbitMQ connection to use.
     * @param topic The topic for this connection.
     * @param serializer The serializer to use.
     */
    protected RabbitConnection(Connection connection, Topic topic, Serializer serializer) {
        this(topic, serializer);

        assert connection != null;

        this.connection = connection;
    }

    /**
     * Sets up the connection for testing. Since no actual connection will be present when using this constructor
     * directly it can only be used for testing purposes.
     * @param topic The topic for this connection.
     * @param serializer The serializer to use.
     */
    protected RabbitConnection(Topic topic, Serializer serializer) {
        assert topic != null;
        assert serializer != null;

        this.topic = topic;
        this.serializer = serializer;

        this.exchangeName = this.topic.getName();

    }

    /**
     * Make a connection to the RabbitMQ broker and sets up the exchange used by this connection.
     * @throws IOException Error while setting up the connection.
     */
    public void connect() throws IOException {
        // Create a channel if not present
        if (!isConnected()) {
            channel = connection.createChannel();
        }

        if (topic instanceof RabbitTopic) {
            RabbitTopic rt = (RabbitTopic) topic;
            // Declare AMQP exchange for the topic using the topics own settings
            channel.exchangeDeclare(exchangeName, rt.getExchangeType(), rt.isPersistent(), !rt.isPersistent(), null);
        } else {
            // Declare AMQP exchange for the topic using sensible defaults
            //   durable: no, we do not need the exchange after a server restart
            //   autoDelete: yes, this exchange is no longer needed when we disconnect
            channel.exchangeDeclare(exchangeName, RabbitTopic.DEFAULT_EXCHANGE_TYPE, false, true, null);
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
     * Returns the topic for this connection.
     * @return The topic for this connection.
     */
    public Topic getTopic() {
        return topic;
    }
}
