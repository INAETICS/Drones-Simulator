package org.inaetics.dronessimulator.pubsub.impl.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
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
    /** The number of milliseconds to wait to reconnect after the connection failed. */
    protected static final int RECONNECT_TIMEOUT = 1000;

    /** The topic for this connection. */
    protected Topic topic;

    /** The RabbitMQ connection used by this subscriber. */
    protected Connection connection;

    /** The connection channel to use, created from the connection. */
    protected Channel channel;

    /** The name of the exchange this connection uses. */
    protected String exchangeName;

    /**
     * Sets up the connection for use.
     * @param connection The RabbitMQ connection to use.
     * @param topic The topic for this connection.
     */
    protected RabbitConnection(Connection connection, Topic topic) {
        assert connection != null;
        assert topic != null;

        this.connection = connection;
        this.topic = topic;

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

        // Declare AMQP exchange for the topic
        //   durable: no, we do not need the exchange after a server restart
        //   autoDelete: yes, this exchange is no longer needed when we disconnect
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT, false, true, null);
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
