package org.inaetics.dronessimulator.pubsub.rabbitmq.common;

import com.rabbitmq.client.*;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.pubsub.api.Topic;
import org.inaetics.dronessimulator.pubsub.api.serializer.Serializer;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import java.io.IOException;
import java.net.ConnectException;
import java.util.*;
import java.util.concurrent.TimeoutException;

/**
 * A wrapper for RabbitMQ connections.
 * <p>
 * This abstract class contains functionality which is shared between the RabbitMQ publisher and subscriber classes
 * related to the connection to the broker.
 */
public abstract class RabbitConnection implements ManagedService {
    /**
     * The maximum number of connection attempts.
     */
    private static final int MAX_CONNECTION_ATTEMPTS = 100;

    /**
     * The timeout between connection attempts in milliseconds.
     */
    private static final long CONNECTION_ATTEMPT_TIMEOUT = 2000;

    /**
     * The connection factory used for setting up a connection.
     */
    private ConnectionFactory connectionFactory;

    /**
     * The RabbitMQ connection used by this subscriber.
     */
    private Connection connection;

    /**
     * The connection channel to use, created from the connection.
     */
    protected Channel channel;

    /**
     * The serializer used in this connection.
     */
    protected volatile Serializer serializer;

    private volatile Discoverer m_discovery;

    private RabbitConnectionInfo connectionInfo;

    /**
     * Collection of topics for which an exchange has been declared.
     */
    private Collection<Topic> declaredTopics;

    /**
     * Sets up the connection for use.
     *
     * @param connectionFactory The RabbitMQ connection factory to use when starting a new connection.
     * @param serializer        The serializer to use.
     */
    protected RabbitConnection(ConnectionFactory connectionFactory, Serializer serializer, Discoverer discoverer) {
        this(connectionFactory);
        assert serializer != null;
        this.serializer = serializer;
        this.m_discovery = discoverer;
    }

    /**
     * Sets up the connection for use within OSGi. This constructor assumes that the serializer is injected later on.
     *
     * @param connectionFactory The RabbitMQ connection factory to use when starting a new connection.
     */
    protected RabbitConnection(ConnectionFactory connectionFactory) {
        this();
        assert connectionFactory != null;
        this.connectionFactory = connectionFactory;
    }

    /**
     * Sets op the connection for use within OSGi. This constructor assumes that the connection factory is built later
     * on with discoverable configs.
     */
    protected RabbitConnection() {
        this.declaredTopics = new HashSet<>();
    }

    /**
     * Updates the connection config based on a discovered configuration.
     *
     * @param config The configuration.
     */
    @Override
    public void updated(Dictionary<String, ?> config) throws ConfigurationException {
        getLogger().debug("Received config: " + String.valueOf(config));

        if (config != null) {
            String username = (String) config.get("username");
            if ("".equals(username)) {
                throw new ConfigurationException("username", "cannot be null or empty");
            }
            String password = (String) config.get("password");
            if ("".equals(password)) {
                throw new ConfigurationException("password", "cannot be null or empty");
            }
            String uri = (String) config.get("uri");
            if ("".equals(uri)) {
                throw new ConfigurationException("uri", "cannot be null or empty");
            }
            this.connectionInfo = new RabbitConnectionInfo(username, password, uri);
        } else {
            this.getLogger().debug("Unset RabbitMQ configuration");
            this.connectionInfo = null;
        }
    }

    /**
     * Make a connection to the RabbitMQ broker and sets up the exchange used by this connection.
     *
     * @throws IOException Error while setting up the connection.
     */
    public void connect() throws IOException {
        // Create a channel if not present
        if (!isConnected()) {
            int attempt = 0;

            while (connection == null) {
                attempt++;

                if (connectionFactory == null){
                    if (connectionInfo == null || !connectionInfo.isValid()) {
                        connectionInfo = RabbitConnectionInfo.createInstance(m_discovery);
                    }
                    try {
                        connectionFactory = connectionInfo.createConnectionFactory();
                    } catch (RabbitConnectionInfo.ConnectionInfoExpiredException e) {
                        connectionInfo = RabbitConnectionInfo.createInstance(m_discovery);
                    }
                }

                try {
                    getLogger().debug(String.format("Try to connect to RabbitMQ using {%s}, {%s} on attempt {%s}", connectionFactory.getUsername(), connectionFactory.getPassword(), String.valueOf(attempt)));
                    connection = connectionFactory.newConnection();

                    getLogger().info("Connected to RabbitMQ");
                } catch (ConnectException | TimeoutException e) {
                    getLogger().error("Could not connect to RabbitMQ (attempt {}): {}", attempt, e);

                    if (attempt >= MAX_CONNECTION_ATTEMPTS) {
                        throw new IOException(e);
                    } else {
                        try {
                            Thread.sleep(CONNECTION_ATTEMPT_TIMEOUT);
                        } catch (InterruptedException e2) {
                            getLogger().fatal(e2);
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }

            channel = connection.createChannel();

            declaredTopics.clear();
        } else {
            getLogger().debug("Attempted to connect to RabbitMQ while already connected");
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
                getLogger().debug("RabbitMQ channel closed");
            }

            connection.close();
            getLogger().info("RabbitMQ connection closed");
        } catch (AlreadyClosedException e) {
            // Good! We are already done.
            getLogger().error("Attempted to disconnect from RabbitMQ while already disconnected.", e);
        } catch (TimeoutException e) {
            getLogger().debug("Error while disconnecting from RabbitMQ: {}", e);
            throw new IOException(e);
        }
    }

    /**
     * Reconnects to the RabbitMQ broker.
     * <p>
     * Calls disconnect and connect in this order.
     */
    public void reconnect() throws IOException {
        getLogger().info("Reconnecting to RabbitMQ");
        // First disconnect, then connect
        this.disconnect();
        this.connect();
    }

    /**
     * Returns whether the connection with the RabbitMQ broker is usable.
     *
     * @return The connection status.
     */
    public boolean isConnected() {
        return connection != null && channel != null && channel.isOpen();
    }

    /**
     * Declares the topic as RabbitMQ exchange using the default settings.
     *
     * @param topic The topic to declare.
     */
    protected void declareTopic(Topic topic) throws IOException {
        // Automatically (re)connect if needed
        if (!isConnected()) {
            this.connect();
        }

        if (!declaredTopics.contains(topic)) {
            Map<String, Object> args = new HashMap<>();
            channel.exchangeDeclare(topic.getName(), BuiltinExchangeType.TOPIC, false);
            declaredTopics.add(topic);
            getLogger().debug("RabbitMQ exchange {} declared", topic.getName());
        }
    }

    /**
     * Returns the AMQP channel used by this connection. May be null if not connected.
     *
     * @return The used AMQP channel.
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Returns the serializer used by this connection. May be null if unset.
     *
     * @return The used serializer.
     */
    public Serializer getSerializer() {
        return serializer;
    }

    /**
     * @return The logger for this class.
     */
    protected abstract Logger getLogger();
}
