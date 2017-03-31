package org.inaetics.dronessimulator.pubsub.rabbitmq;


import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.Topic;
import org.inaetics.dronessimulator.pubsub.rabbitmq.publisher.PublisherRunner;
import org.inaetics.dronessimulator.pubsub.rabbitmq.subscriber.SubscriberRunner;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Test runner for the RabbitMQ publisher/subscriber implementation.
 */
public class RabbitTest {
    ConnectionFactory connection;
    Topic topic;

    @Before
    public void setUp() throws Exception {
        // Set up connection
        connection = new ConnectionFactory();

        // Set up topic
        topic = new TestTopic();
    }

    @Test
    public void full() throws Exception {
        // Define some messages
        TestMessage message1 = new TestMessage("This is the first message.");
        TestMessage message2 = new TestMessage("This is the second message.");
        TestMessage message3 = new TestMessage("This is the third message.");

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message1);
        messages.add(message2);
        messages.add(message3);

        // Set up publisher and subscriber
        long timeout = PublisherRunner.SLEEP_TIME * (messages.size() + 3);
        PublisherRunner pub = new PublisherRunner(this.connection.newConnection(), this.topic, messages);
        SubscriberRunner sub = new SubscriberRunner(this.connection.newConnection(), this.topic, timeout);

        // Run
        Thread pubT = new Thread(pub);
        Thread subT = new Thread(sub);
        pubT.start();
        subT.start();

        // Wait until finished
        pubT.join();
        subT.join();

        // Check messages on subscriber
        ArrayList<Message> receivedMessages = sub.getTestMessages();

        assertTrue("No message was received.", receivedMessages.size() > 0);
        assertEquals(message1, receivedMessages.get(0));
        assertTrue("Too few messages received.", receivedMessages.size() > 1);
        assertEquals(message2, receivedMessages.get(1));
        assertTrue("Too few messages received.", receivedMessages.size() > 2);
        assertEquals(message3, receivedMessages.get(2));
    }
}
