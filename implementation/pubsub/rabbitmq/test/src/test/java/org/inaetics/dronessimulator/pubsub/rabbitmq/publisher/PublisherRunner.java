package org.inaetics.dronessimulator.pubsub.rabbitmq.publisher;

import com.rabbitmq.client.ConnectionFactory;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.Topic;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.inaetics.dronessimulator.pubsub.javaserializer.JavaSerializer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Runner for the RabbitMQ publisher for use in tests.
 */
public class PublisherRunner implements Runnable {
    /** The time to wait between messages. */
    public static final long SLEEP_TIME = 1000;

    /** The publisher under test. */
    private final RabbitPublisher publisher;

    /** The topic to send test messages on. */
    private final Topic topic;

    /** List of messages to send. */
    private final ArrayList<Message> testMessages;

    /**
     * Create the logger
     */
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(PublisherRunner.class);
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

            log.info(String.format("Publisher %s connected", topic.getName()));

            for (Message message : this.testMessages) {
                if (!publisher.isConnected()) log.warn("Publisher is not connected");
                Thread.sleep(SLEEP_TIME);
                this.publisher.send(this.topic, message);
                log.debug(String.format("Publisher %s sent message %s at %d", this.topic.getName(), message.toString(), System.currentTimeMillis()));
            }

            log.info(String.format("Publisher %s is done, disconnecting", topic.getName()));

            this.publisher.disconnect();
        } catch (IOException | InterruptedException e) {
            log.error(e);
        }
    }
}
