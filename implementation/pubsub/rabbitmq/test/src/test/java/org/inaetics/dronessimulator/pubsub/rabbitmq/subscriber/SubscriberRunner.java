package org.inaetics.dronessimulator.pubsub.rabbitmq.subscriber;

import com.rabbitmq.client.ConnectionFactory;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.Topic;
import org.inaetics.dronessimulator.pubsub.javaserializer.JavaSerializer;
import org.inaetics.dronessimulator.pubsub.rabbitmq.TestMessage;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

/**
 * Runner for the RabbitMQ subscriber for use in tests.
 */
public class SubscriberRunner implements Runnable {
    /** The identifier of the subscriber queue. */
    private String identifier;

    /** The subscriber under test. */
    private RabbitSubscriber subscriber;

    /** The topic to receive test messages from. */
    private Topic topic;

    /** List of received messages. */
    private ArrayList<Message> testMessages;

    /** Time to wait for messages. */
    private long timeout;

    /**
     * @param connectionFactory The connection settings to use for tests.
     * @param topic The topic to receive test messages from.
     * @param timeout The time to wait for incoming messages.
     */
    public SubscriberRunner(ConnectionFactory connectionFactory, Topic topic, String identifier, long timeout) {
        this.identifier = identifier;
        this.topic = topic;
        this.testMessages = new ArrayList<>();
        this.timeout = timeout;
        this.subscriber = new RabbitSubscriber(connectionFactory, identifier, new JavaSerializer());
    }

    @Override
    public void run() {
        try {
            this.subscriber.addHandler(TestMessage.class, new TestHandler());
            this.subscriber.connect();
            this.subscriber.addTopic(this.topic);

            System.out.printf("Subscriber %s is connected\n", identifier);
            assertTrue("Publisher is not connected", subscriber.isConnected());

            Thread.sleep(timeout);

            System.out.printf("Subscriber %s is done, disconnecting\n", identifier);

            this.subscriber.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
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
            System.out.printf("Subscriber %s received message %s at %d\n", identifier, message.toString(), System.currentTimeMillis());
            testMessages.add(message);
        }
    }
}
