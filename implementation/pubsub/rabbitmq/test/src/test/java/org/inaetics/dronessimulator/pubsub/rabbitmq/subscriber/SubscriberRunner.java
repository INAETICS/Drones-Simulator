package org.inaetics.dronessimulator.pubsub.rabbitmq.subscriber;

import com.rabbitmq.client.ConnectionFactory;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import org.apache.logging.log4j.ThreadContext;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.Topic;
import org.inaetics.dronessimulator.pubsub.javaserializer.JavaSerializer;
import org.inaetics.dronessimulator.pubsub.rabbitmq.TestMessage;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

/**
 * Runner for the RabbitMQ subscriber for use in tests.
 */
@Log4j
public class SubscriberRunner implements Runnable {
    /** The identifier of the subscriber queue. */
    private final String identifier;

    /** The subscriber under test. */
    @Getter
    private final RabbitSubscriber subscriber;

    /** The topic to receive test messages from. */
    private final Topic topic;

    /** List of received messages. */
    private final ArrayList<Message> testMessages;

    /** Time to wait for messages. */
    private final long timeout;
    private final Callable<Boolean> awaitUntil;
    private boolean ready;

    /**
     * @param connectionFactory The connection settings to use for tests.
     * @param topic             The topic to receive test messages from.
     * @param timeout           The time to wait for incoming messages.
     */
    public SubscriberRunner(ConnectionFactory connectionFactory, Topic topic, String identifier, long timeout, Discoverer discoverer) {
        this.identifier = identifier;
        this.topic = topic;
        this.testMessages = new ArrayList<>();
        this.timeout = timeout;
        this.awaitUntil = null;
        this.subscriber = new RabbitSubscriber(connectionFactory, identifier, new JavaSerializer(), discoverer);
    }

    /**
     * @param connectionFactory The connection settings to use for tests.
     * @param topic             The topic to receive test messages from.
     * @param timeout           The maximal time to wait for incoming messages.
     * @param awaitUntil        The condition to pass for the runner to finish.
     */
    public SubscriberRunner(ConnectionFactory connectionFactory, Topic topic, String identifier, long timeout, Callable<Boolean> awaitUntil, Discoverer discoverer) {
        this.identifier = identifier;
        this.topic = topic;
        this.testMessages = new ArrayList<>();
        this.timeout = timeout;
        this.awaitUntil = awaitUntil;
        this.subscriber = new RabbitSubscriber(connectionFactory, identifier, new JavaSerializer(), discoverer);
        ThreadContext.put("threadId", identifier);
    }

    @Override
    public void run() {
        try {
            this.subscriber.addHandler(TestMessage.class, new TestHandler());
            this.subscriber.connect();
            this.subscriber.addTopic(this.topic);

            log.info(String.format("Subscriber %s is connected", identifier));
            if (!subscriber.isConnected()) log.warn("Subscriber is not connected");
            ready = true;

            if (awaitUntil == null)
                Thread.sleep(timeout);
            else
                await().atMost(timeout, TimeUnit.MILLISECONDS).until(awaitUntil);

            log.info(String.format("Subscriber %s is done with %d messages, disconnecting", identifier, testMessages.size()));

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

    public boolean isReady() {
        return ready;
    }

    class TestHandler implements MessageHandler {
        @Override
        public synchronized void handleMessage(Message message) {
            log.debug(String.format("Subscriber %s received message %s at %d\n", identifier, message.toString(), System.currentTimeMillis()));
            testMessages.add(message);
        }

        @Override
        public String toString() {
            return super.toString() + " for " + identifier;
        }
    }
}
