package org.inaetics.dronessimulator.pubsub.impl.broker.rabbitmq;

import com.rabbitmq.client.Connection;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.broker.Publisher;
import org.inaetics.dronessimulator.pubsub.api.serializer.Serializer;
import org.inaetics.dronessimulator.pubsub.api.broker.Topic;

import java.io.IOException;

public class RabbitPublisher extends RabbitConnection implements Publisher {
    /**
     * Instantiates a new RabbitMQ publisher for the given topic.
     * @param connection The RabbitMQ connection to use.
     * @param topic The topic this publisher publishes to.
     * @param serializer The serializer to use.
     */
    public RabbitPublisher(Connection connection, Topic topic, Serializer serializer) {
        super(connection, topic, serializer);
    }

    /**
     * Sends the given message to subscribers on the topic of this publisher.
     * @param message The message to send.
     */
    public void send(Message message) {
        try {
            // Automatically (re)connect if needed
            if (!this.isConnected()) {
                this.connect();
            }

            if (message != null) {
                this.channel.basicPublish(this.exchangeName, "", null, serializer.serialize(message));
            }
        } catch (IOException ignore) {
            // Just drop the message if there is no good connection
        }
    }
}
