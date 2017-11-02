package org.inaetics.dronessimulator.pubsub.rabbitmq.publisher;

import com.rabbitmq.client.ConnectionFactory;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.pubsub.javaserializer.JavaSerializer;
import org.inaetics.dronessimulator.pubsub.protocol.Message;
import org.inaetics.dronessimulator.pubsub.protocol.Topic;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

/**
 * Runner for the RabbitMQ publisher for use in tests.
 */
public class PublisherRunner implements Runnable {
    /** The time to wait between messages. */
    public static final long SLEEP_TIME = 1000;

    /** The publisher under test. */
    private RabbitPublisher publisher;

    /** The topic to send test messages on. */
    private Topic topic;

    /** List of messages to send. */
    private ArrayList<Message> testMessages;

    /**
     * @param connectionFactory The connection settings to use for tests.
     * @param topic The topic to publish test messages to.
     * @param testMessages The messages to test.
     */
    public PublisherRunner(ConnectionFactory connectionFactory, Topic topic, ArrayList<Message> testMessages, Discoverer discoverer) {
        this.publisher = new RabbitPublisher(connectionFactory, new JavaSerializer(), discoverer);
        this.topic = topic;
        this.testMessages = (ArrayList<Message>) testMessages.clone();
    }

    @Override
    public void run() {
        try {
            this.publisher.connect();

            System.out.printf("Publisher %s connected\n", topic.getName());

            for (Message message : this.testMessages) {
                assertTrue("Publisher is not connected", publisher.isConnected());
                Thread.sleep(SLEEP_TIME);
                this.publisher.send(this.topic, message);
                System.out.printf("Publisher %s sent message %s at %d\n", this.topic.getName(), message.toString(), System.currentTimeMillis());
            }

            System.out.printf("Publisher %s is done, disconnecting\n", topic.getName());

            this.publisher.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
