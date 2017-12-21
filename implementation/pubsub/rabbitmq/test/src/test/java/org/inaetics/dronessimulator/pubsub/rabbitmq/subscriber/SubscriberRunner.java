package org.inaetics.dronessimulator.pubsub.rabbitmq.subscriber;

import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.Topic;
import org.inaetics.dronessimulator.pubsub.javaserializer.JavaSerializer;
import org.inaetics.dronessimulator.pubsub.rabbitmq.TestMessage;

import java.util.ArrayList;

/**
 * Runner for the RabbitMQ subscriber for use in tests.
 */
@Log4j
public class SubscriberRunner implements Runnable {
    /** The identifier of the subscriber queue. */
    private final String identifier;

    /** The subscriber under test. */
    private final RabbitSubscriber subscriber;

    /** The topic to receive test messages from. */
    private final Topic topic;

    /** List of received messages. */
    private final ArrayList<Message> testMessages;

    /** Time to wait for messages. */
    private final long timeout;

    /**
     * @param connectionFactory The connection settings to use for tests.
     * @param topic The topic to receive test messages from.
     * @param timeout The time to wait for incoming messages.
     */
    public SubscriberRunner(ConnectionFactory connectionFactory, Topic topic, String identifier, long timeout, Discoverer discoverer) {
        this.identifier = identifier;
        this.topic = topic;
        this.testMessages = new ArrayList<>();
        this.timeout = timeout;
        this.subscriber = new RabbitSubscriber(connectionFactory, identifier, new JavaSerializer(), discoverer);
    }

    @Override
    public void run() {
        try {
            this.subscriber.addHandler(TestMessage.class, new TestHandler());
            this.subscriber.connect();
            this.subscriber.addTopic(this.topic);

            log.info(String.format("Subscriber %s is connected", identifier));
            if (!subscriber.isConnected()) log.warn("Subscriber is not connected");

            Thread.sleep(timeout);

            log.info(String.format("Subscriber %s is done, disconnecting", identifier));

            this.subscriber.disconnect();
        } catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * @return The received messages.
     */
    public synchronized ArrayList<Message> getTestMessages() {
        return (ArrayList<Message>) this.testMessages.clone();
    }

    class TestHandler implements MessageHandler {
        @Override
        public synchronized void handleMessage(Message message) {
            log.debug(String.format("Subscriber %s received message %s at %d\n", identifier, message.toString(), System.currentTimeMillis()));
            testMessages.add(message);
        }
    }
}
