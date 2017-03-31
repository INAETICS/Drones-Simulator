package org.inaetics.dronessimulator.pubsub.rabbitmq.publisher;

import com.rabbitmq.client.Connection;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.Topic;
import org.inaetics.dronessimulator.pubsub.javaserializer.JavaSerializer;

import java.util.ArrayList;

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
     * @param connection The connection to use for tests.
     * @param topic The topic to publish test messages to.
     * @param testMessages The messages to test.
     */
    public PublisherRunner(Connection connection, Topic topic, ArrayList<Message> testMessages) {
        this.publisher = new RabbitPublisher(connection, new JavaSerializer());
        this.topic = topic;
        this.testMessages = (ArrayList<Message>) testMessages.clone();
    }

    @Override
    public void run() {
        try {
            this.publisher.connect();

            for (Message message : this.testMessages) {
                Thread.sleep(SLEEP_TIME);
                this.publisher.send(this.topic, message);
                System.out.printf("Sent message %s at %d\n", message.toString(), System.currentTimeMillis());
            }

            this.publisher.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
