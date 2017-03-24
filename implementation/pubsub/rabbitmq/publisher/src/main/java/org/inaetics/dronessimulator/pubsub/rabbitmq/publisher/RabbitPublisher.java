package org.inaetics.dronessimulator.pubsub.rabbitmq.publisher;

import com.rabbitmq.client.Connection;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.inaetics.dronessimulator.pubsub.api.serializer.Serializer;
import org.inaetics.dronessimulator.pubsub.api.Topic;
import org.inaetics.dronessimulator.pubsub.rabbitmq.common.RabbitConnection;

import java.io.IOException;
import java.io.Serializable;

/**
 * A RabbitMQ implementation of a publisher.
 */
public class RabbitPublisher extends RabbitConnection implements Publisher {
    /**
     * Instantiates a new RabbitMQ publisher.
     * @param connection The RabbitMQ connection to use.
     * @param serializer The serializer to use.
     */
    public RabbitPublisher(Connection connection, Serializer serializer) {
        super(connection, serializer);
    }

    /**
     * Instantiates a new RabbitMQ publisher for use with OSGi. This constructor assumes the serializer is injected
     * later on.
     * @param connection The connection to use.
     */
    public RabbitPublisher(Connection connection) {
        super(connection);
    }

    /**
     * Sends the given message to subscribers on the topic of this publisher.
     * @param topic The topic to publish the message to.
     * @param message The message to send.
     */
    public void send(Topic topic, Message message) {
        try {
            // Automatically (re)connect if needed
            if (!this.isConnected()) {
                this.connect();
            }

            // Declare topic, declares exchange on message broker if needed
            this.declareTopic(topic);

            // Drop null messages and when a serializer is absent
            Serializer serializer = this.serializer;

            if (message != null && serializer != null) {
                byte[] serializedMessage = serializer.serialize(message);
                this.channel.basicPublish(topic.getName(), "", null, serializedMessage);
            }
        } catch (IOException ignore) {
            // Just drop the message if there is no good connection
        }
    }
}
