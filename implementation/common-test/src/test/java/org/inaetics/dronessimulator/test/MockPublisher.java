package org.inaetics.dronessimulator.test;

import org.inaetics.dronessimulator.common.Tuple;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.Topic;
import org.inaetics.pubsub.api.pubsub.MultipartException;
import org.inaetics.pubsub.api.pubsub.Publisher;
import org.inaetics.pubsub.api.pubsub.Subscriber;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class MockPublisher implements Publisher {
    public MockPublisher(Subscriber subscriber, List<Object> receivedMessages) {
        this.subscriber = subscriber;
        this.receivedMessages = receivedMessages;
    }

    public MockPublisher() {
    }

    private Subscriber subscriber;

    private List<Object> receivedMessages = new LinkedList<>();

    public List<Object> getReceivedMessages() {
        return receivedMessages;
    }

    @Override
    public void send(Object message) {
        receivedMessages.add(message);
        if (subscriber != null) {
            subscriber.receive(message, null);
        }
    }

    @Override
    public void send(Object o, int i) {
        throw new NotImplementedException();
    }

    @Override
    public void sendMultipart(Object o, int i) throws MultipartException {
        throw new NotImplementedException();
    }

    @Override
    public void sendMultipart(Object o, int i, int i1) throws MultipartException {
        throw new NotImplementedException();
    }

    @Override
    public int localMsgTypeIdForMsgType(String s) {
        throw new NotImplementedException();
    }

    public boolean isMessageReceived(Message message) {
        return receivedMessages.contains(message);
    }

    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }
}
