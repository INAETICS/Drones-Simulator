package org.inaetics.dronessimulator.pubsub.impl.rabbitmq;

import com.rabbitmq.client.Connection;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.Publisher;
import org.inaetics.dronessimulator.pubsub.api.Topic;

import java.io.IOException;

public class RabbitPublisher extends RabbitConnection implements Publisher {
    /**
     * Instantiates a new RabbitMQ publisher for the given topic.
     * @param connection The RabbitMQ connection to use.
     * @param topic The topic this publisher publishes to.
     */
    public RabbitPublisher(Connection connection, Topic topic) {
        super(connection, topic);
    }

    public void send(Message message) {
        try {
            // Automatically (re)connect if needed
            if (!this.isConnected()) {
                this.connect();
            }

            if (message != null) {
                this.channel.basicPublish(this.exchangeName, "", null, null); // TODO Actually implement body
            }
        } catch (IOException ignore) {
            // Just drop the message if there is no good connection
        }
    }
}
