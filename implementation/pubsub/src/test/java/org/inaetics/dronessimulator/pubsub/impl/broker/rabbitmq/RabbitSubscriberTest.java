package org.inaetics.dronessimulator.pubsub.impl.broker.rabbitmq;

import org.inaetics.dronessimulator.pubsub.api.serializer.Serializer;
import org.inaetics.dronessimulator.pubsub.impl.serializer.java.JavaSerializer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test case for the RabbitMQ subscriber implementation.
 */
class RabbitSubscriberTest {
    @Test
    void receive() throws IOException, TimeoutException {
        // Initialize subscriber
        String topicName = "testTopic";
        String queueName = "testQueue";

        RabbitTopic topic = new RabbitTopic(topicName);
        Serializer serializer = new JavaSerializer();
        RabbitSubscriber subscriber = new RabbitSubscriber(topic, queueName, serializer);

        // Initialize handlers
        TestMessageHandler handler = new TestMessageHandler();
        TestIntMessageHandler intHandler = new TestIntMessageHandler();
        subscriber.addHandler(TestMessage.class, handler);
        subscriber.addHandler(TestIntMessage.class, intHandler);

        // Write messages
        String message = "This is a test message.";
        int intMessage = 42;
        subscriber.receive(new TestMessage(message));
        subscriber.receive(new TestIntMessage(intMessage));

        // Check whether the correct handler was called
        assertEquals(message, handler.getMessage().getMessage());
        assertEquals(intMessage, intHandler.getMessage().getMessage());
    }

}