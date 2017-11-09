package org.inaetics.dronessimulator.pubsub.rabbitmq;


import com.rabbitmq.client.ConnectionFactory;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.Topic;
import org.inaetics.dronessimulator.pubsub.rabbitmq.common.RabbitConnectionInfo;
import org.inaetics.dronessimulator.pubsub.rabbitmq.publisher.PublisherRunner;
import org.inaetics.dronessimulator.pubsub.rabbitmq.subscriber.SubscriberRunner;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Test runner for the RabbitMQ publisher/subscriber implementation.
 *
 * WARNING: The tests included in this class expect to find a running RabbitMQ instance without password protection on
 * localhost using the default port.
 */
public class RabbitIT {
    private static final String LIPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed ac magna sit a" +
            "met erat blandit tincidunt. Phasellus ornare neque sem, sit amet interdum magna tincidunt viverra. Praes" +
            "ent vulputate tortor a pretium malesuada. Cras feugiat, diam vel aliquam porttitor, risus arcu varius ne" +
            "que, vel dignissim velit elit in massa. Nullam blandit turpis vitae felis lobortis, quis feugiat diam pe" +
            "llentesque. Mauris nibh leo, lacinia in turpis nec, pharetra sagittis diam. Praesent sit amet mi lacus. " +
            "Proin lorem eros, dignissim id tempor et, consectetur ac lorem. Sed lacinia maximus massa et varius. Pha" +
            "sellus quis varius ligula, ac placerat tortor. In ac tincidunt diam. Donec bibendum nunc nec mauris tris" +
            "tique placerat. Maecenas consectetur, nunc in facilisis viverra, ligula neque consectetur tellus, sit am" +
            "et maximus ex metus sed odio. Quisque at neque nec neque tempor porttitor vel ac libero.";

    ConnectionFactory connection;

    @Before
    public void setUp() throws Exception {
        // Set up connection
        connection = new ConnectionFactory();
        connection.setUsername("yourUser");
        connection.setPassword("yourPass");
    }

    /**
     * Tests a setup with one publisher and one subscriber.
     *
     * This test checks the number of received messages, their contents and whether the order is preserved.
     */
    @Test
    public void oneToOne() throws Exception {
        Topic topic = new TestTopic("oneToOne");

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
        PublisherRunner pub = new PublisherRunner(this.connection, topic, messages, null);
        SubscriberRunner sub = new SubscriberRunner(this.connection, topic, "sub", timeout, null);

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

        this.assertMessages(messages, receivedMessages);
    }

    /**
     * This test tests a setup with a single publisher and multiple subscribers.
     */
    @Test
    public void oneToMany() throws Exception {
        Topic topic = new TestTopic("oneToMany");

        // Define some messages
        TestMessage message1 = new TestMessage("This is the first message.");
        TestMessage message2 = new TestMessage("This is the second message.");
        TestMessage message3 = new TestMessage("This is the third message.");
        TestMessage message4 = new TestMessage(LIPSUM);
        TestMessage message5 = new TestMessage("This is the last message.");

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message1);
        messages.add(message2);
        messages.add(message3);
        messages.add(message4);
        messages.add(message5);

        // Set up publisher and subscribers
        long timeout = PublisherRunner.SLEEP_TIME * (messages.size() + 3);
        PublisherRunner pub = new PublisherRunner(this.connection, topic, messages, null);
        SubscriberRunner sub1 = new SubscriberRunner(this.connection, topic, "sub1", timeout, null);
        SubscriberRunner sub2 = new SubscriberRunner(this.connection, topic, "sub2", timeout, null);
        SubscriberRunner sub3 = new SubscriberRunner(this.connection, topic, "sub3", timeout, null);

        // Run
        Thread pubT = new Thread(pub);
        Thread sub1T = new Thread(sub1);
        Thread sub2T = new Thread(sub2);
        Thread sub3T = new Thread(sub3);
        pubT.start();
        sub1T.start();
        sub2T.start();
        sub3T.start();

        // Wait until finished
        pubT.join();
        sub1T.join();
        sub2T.join();
        sub3T.join();

        this.assertMessages(messages, sub1.getTestMessages());
        this.assertMessages(messages, sub2.getTestMessages());
        this.assertMessages(messages, sub3.getTestMessages());
    }

    /**
     * Helper method that asserts whether the actual messages are as expected.
     *
     * This method checks whether the number of messages is equal and the content of each message is identical. It
     * assumes (and therefore tests) the order of the messages in both lists is equal.
     */
    public void assertMessages(ArrayList<Message> expected, ArrayList<Message> actual) {
        // Check length, test stops if size is not equal (so we can assume the size IS equal below)
        assertEquals(expected.size(), actual.size());

        // For each message, check its counterpart for equality
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }
}
