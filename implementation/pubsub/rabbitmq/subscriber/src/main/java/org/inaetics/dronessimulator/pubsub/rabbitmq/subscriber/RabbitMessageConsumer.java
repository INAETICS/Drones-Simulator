package org.inaetics.dronessimulator.pubsub.rabbitmq.subscriber;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.serializer.Serializer;

import java.io.IOException;

/**
 * A RabbitMQ consumer to work in conjunction with the RabbitMQ subscriber.
 */
class RabbitMessageConsumer extends DefaultConsumer {
    /** A RabbitMQ subscriber instance. */
    private RabbitSubscriber subscriber;

    /**
     * Instantiates a new RabbitMQ consumer based on the given subscriber.
     * @param subscriber The subscriber related to this consumer.
     */
    RabbitMessageConsumer(RabbitSubscriber subscriber) {
        super(subscriber.getChannel());
        this.subscriber = subscriber;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        Serializer serializer = subscriber.getSerializer();

        // Check if we have a serializer, otherwise just ignore the message
        if (serializer != null) {
            try {
                Message message = serializer.deserialize(body);
                if (getChannel().isOpen()) this.getChannel().basicAck(envelope.getDeliveryTag(), false);
                subscriber.receive(message);
            } catch (ClassNotFoundException e) {
                // Reject the message since we cannot do anything useful with it
                if (getChannel().isOpen()) this.getChannel().basicNack(envelope.getDeliveryTag(), false, false);
                subscriber.getLogger().warn("Received message of unknown type, message dropped", e);
            }
        }
    }
}
