package org.inaetics.dronessimulator.pubsub.api.publisher;

import lombok.Getter;
import org.inaetics.dronessimulator.common.Tuple;
import org.inaetics.dronessimulator.pubsub.protocol.Message;
import org.inaetics.dronessimulator.pubsub.protocol.Topic;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class MockPublisher implements Publisher {
    @Getter
    private List<Tuple<Topic, Message>> receivedMessages = new LinkedList<>();

    @Override
    public void send(Topic topic, Message message) throws IOException {
        receivedMessages.add(new Tuple<>(topic, message));
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
