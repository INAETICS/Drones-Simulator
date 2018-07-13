package org.inaetics.dronessimulator.test;

import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.Topic;
import org.inaetics.pubsub.api.pubsub.Subscriber;

import java.io.IOException;
import java.util.*;

public class MockSubscriber implements Subscriber {
    private List<Object> receivedMessages = new ArrayList<>();

    @Override
    public void receive(Object o, MultipartCallbacks multipartCallbacks) {
        receivedMessages.add(o);
    }

    public List<Object> getReceivedMessages() {
        return Collections.unmodifiableList(receivedMessages);
    }
}
