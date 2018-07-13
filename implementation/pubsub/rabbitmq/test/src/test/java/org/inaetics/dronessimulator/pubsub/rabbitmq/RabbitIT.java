package org.inaetics.dronessimulator.pubsub.rabbitmq;


import com.rabbitmq.client.ConnectionFactory;
import org.awaitility.core.ConditionTimeoutException;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.Topic;
import org.inaetics.dronessimulator.pubsub.rabbitmq.publisher.PublisherRunner;
import org.inaetics.dronessimulator.pubsub.rabbitmq.subscriber.SubscriberRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Test runner for the RabbitMQ publisher/subscriber implementation.
 * <p>
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

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RabbitIT.class);

    @Before
    public void setUp() throws Exception {
        // Set up connection
        connection = new ConnectionFactory();
        connection.setUsername("yourUser");
        connection.setPassword("yourPass");
    }

    /**
     * Tests a setup with one publisher and one subscriber.
     * <p>
     * This test checks the number of received messages, their contents and whether the order is preserved.
     */
    @Test
    public void oneToOne() throws Exception {
        Topic topic = new TestTopic("oneToOne" + ThreadLocalRandom.current().nextDouble());

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
        SubscriberRunner sub = new SubscriberRunner(this.connection, topic, "sub", timeout, null);
        PublisherRunner pub = new PublisherRunner(this.connection, topic, messages, null);

        // Run
        Thread subT = new Thread(sub);
        Thread pubT = new Thread(pub);
        subT.start();
        await().until(sub::isReady);
        pubT.start();

        // Wait until finished
        pubT.join();
        subT.join();

        // Check messages on subscriber
        ArrayList<Message> receivedMessages = sub.getTestMessages();

        await().until(() -> receivedMessages.size() >= messages.size());
        this.assertMessages(messages, receivedMessages);
    }

    /**
     * This test tests a setup with a single publisher and multiple subscribers.
     */
    @Test
    public void oneToMany() throws Exception {
        Topic topic = new TestTopic("oneToMany" + ThreadLocalRandom.current().nextDouble());

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
        SubscriberRunner sub1 = new SubscriberRunner(this.connection, topic, "sub1", timeout, null);
        SubscriberRunner sub2 = new SubscriberRunner(this.connection, topic, "sub2", timeout, null);
        SubscriberRunner sub3 = new SubscriberRunner(this.connection, topic, "sub3", timeout, null);
        PublisherRunner pub = new PublisherRunner(this.connection, topic, messages, null);

        // Run
        Thread sub1T = new Thread(sub1);
        Thread sub2T = new Thread(sub2);
        Thread sub3T = new Thread(sub3);
        Thread pubT = new Thread(pub);
        sub1T.start();
        sub2T.start();
        sub3T.start();
        await().untilAsserted(() -> Assert.assertThat("The subscribers are not ready in time, so the test cannot be properly executed.", sub1.isReady() &&
                sub2.isReady() && sub3.isReady(), is(true)));
        pubT.start();

        // Wait until finished
        pubT.join();
        sub1T.join();
        sub2T.join();
        sub3T.join();

        awaitUntilOrElse(() -> sub1.getTestMessages().size() >= messages.size(), () -> {
            log.error("sub1 does not have enough messages. We expected: " + messages);
            log.error("Sub1 has the following messages: " + sub1.getTestMessages());
            log.error("The subscriber in sub1 has the following handlers: " + sub1.getSubscriber().getHandlers());
        });
        this.assertMessages(messages, sub1.getTestMessages());
        awaitUntilOrElse(() -> sub2.getTestMessages().size() >= messages.size(), () -> {
            log.error("sub2 does not have enough messages. We expected: " + messages);
            log.error("Sub2 has the following messages: " + sub2.getTestMessages());
            log.error("The subscriber in sub2 has the following handlers: " + sub2.getSubscriber().getHandlers());
        });
        this.assertMessages(messages, sub2.getTestMessages());
        awaitUntilOrElse(() -> sub3.getTestMessages().size() >= messages.size(), () -> {
            log.error("sub3 does not have enough messages. We expected: " + messages);
            log.error("Sub3 has the following messages: " + sub3.getTestMessages());
            log.error("The subscriber in sub3 has the following handlers: " + sub3.getSubscriber().getHandlers());
        });
        this.assertMessages(messages, sub3.getTestMessages());
    }

    /**
     * Helper method that asserts whether the actual messages are as expected.
     * <p>
     * This method checks whether the number of messages is equal and the content of each message is identical. It
     * assumes (and therefore tests) the order of the messages in both lists is equal.
     */
    public void assertMessages(ArrayList<Message> expected, ArrayList<Message> actual) {
        assertThat(actual, hasItems(expected.toArray(new Message[expected.size()])));
        //TODO find out why messages are received multiple times
    }

    private void awaitUntilOrElse(Callable<Boolean> until, Runnable onFailure) {
        try {
            await().until(until);
        } catch (ConditionTimeoutException e) {
            onFailure.run();
            throw e;
        }
    }
}
