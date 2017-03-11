package org.inaetics.dronessimulator.pubsub.impl.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

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
        super(subscriber.channel);
        this.subscriber = subscriber;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        try {
            subscriber.receive(subscriber.serializer.deserialize(body));
        } catch (NoClassDefFoundError ignore) {
            // Just drop the message if we cannot deserialize it
        }
        this.getChannel().basicAck(envelope.getDeliveryTag(), false);
    }
}
