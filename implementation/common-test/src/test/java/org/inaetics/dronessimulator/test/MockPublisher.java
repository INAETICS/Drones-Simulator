package org.inaetics.dronessimulator.test;

import org.inaetics.dronessimulator.common.Tuple;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.Topic;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class MockPublisher implements Publisher {
    public MockPublisher(Subscriber subscriber, List<Tuple<Topic, Message>> receivedMessages) {
        this.subscriber = subscriber;
        this.receivedMessages = receivedMessages;
    }

    public MockPublisher() {
    }

    private Subscriber subscriber;

    private List<Tuple<Topic, Message>> receivedMessages = new LinkedList<>();

    public List<Tuple<Topic, Message>> getReceivedMessages() {
        return receivedMessages;
    }

    @Override
    public void send(Topic topic, Message message) throws IOException {
        receivedMessages.add(new Tuple<>(topic, message));
        if (subscriber != null) {
            subscriber.receive(message);
        }
    }

    public boolean isMessageReceived(Topic topic, Message message) {
        if (receivedMessages.contains(new Tuple<>(topic, message))) {
            return true;
        } else {
            for (Tuple<Topic, Message> receivedMessage : receivedMessages) {
                if (receivedMessage.getLeft().equals(topic) && receivedMessage.getRight().equals(message)) {
                    return true;
                }
            }
        }
        return false;
    }
}
