package org.inaetics.dronessimulator.pubsub.rabbitmq.subscriber;

import com.rabbitmq.client.Connection;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.Topic;
import org.inaetics.dronessimulator.pubsub.javaserializer.JavaSerializer;
import org.inaetics.dronessimulator.pubsub.rabbitmq.TestMessage;
import org.inaetics.dronessimulator.pubsub.rabbitmq.publisher.RabbitPublisher;

import java.util.ArrayList;

/**
 * Runner for the RabbitMQ subscriber for use in tests.
 */
public class SubscriberRunner implements Runnable {
    /** The identifier of the subscriber queue. */
    private static final String IDENTIFIER = "testSubscriber";

    /** The subscriber under test. */
    private RabbitSubscriber subscriber;

    /** The topic to receive test messages from. */
    private Topic topic;

    /** List of received messages. */
    private ArrayList<Message> testMessages;

    /** Time to wait for messages. */
    private long timeout;

    /**
     * @param connection The connection to use for tests.
     * @param topic The topic to receive test messages from.
     * @param timeout The time to wait for incoming messages.
     */
    public SubscriberRunner(Connection connection, Topic topic, long timeout) {
        this.subscriber = new RabbitSubscriber(connection, IDENTIFIER, new JavaSerializer());
        this.topic = topic;
        this.testMessages = new ArrayList<>();
        this.timeout = timeout;
    }

    @Override
    public void run() {
        try {
            this.subscriber.addHandler(TestMessage.class, new TestHandler());
            this.subscriber.connect();
            this.subscriber.addTopic(this.topic);

            Thread.sleep(timeout);

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
            System.out.printf("Received message %s at %d\n", message.toString(), System.currentTimeMillis());
            testMessages.add(message);
        }
    }
}
