package org.inaetics.dronessimulator.pubsub.rabbitmq.subscriber;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.inaetics.dronessimulator.pubsub.api.serializer.Serializer;

import java.io.IOException;

/**
 * A RabbitMQ consumer to work in conjunction with the RabbitMQ subscriber.
 */
class RabbitMessageConsumer extends DefaultConsumer implements Runnable {
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
        System.out.println("[subscriber] Handle Delivery");
        Serializer serializer = subscriber.getSerializer();

        // Check if we have a serializer, otherwise just ignore the message
        if (serializer != null) {
            try {
                subscriber.receive(serializer.deserialize(body));
                this.getChannel().basicAck(envelope.getDeliveryTag(), false);
                System.out.println("Acked");
            } catch (ClassNotFoundException ignore) {
                // Reject the message since we cannot do anything useful with it
                this.getChannel().basicNack(envelope.getDeliveryTag(), false, false);
                System.out.println("Not-Acked");
            }
        }
    }

    @Override
    public void run() {
        System.out.println("[subscriber] Starting rabbitmessageconsumer");
        Channel channel = this.subscriber.getChannel();

        try {
            while (!Thread.interrupted()) {
                // Breaks out of while loop with IOException in case the channel is closed
                System.out.println("[Subscriber] before basic consume");
                channel.basicConsume(this.subscriber.getIdentifier(), false, this);
                System.out.println("[Subscriber] basic consume");
            }
        } catch (IOException ignored) {
            ignored.printStackTrace();
            // Connection is closed, maybe split this and ShutdownSignalException and log some stuff
        }
    }
}
