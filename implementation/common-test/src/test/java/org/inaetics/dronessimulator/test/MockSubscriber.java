package org.inaetics.dronessimulator.test;

import lombok.Getter;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.Topic;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import java.io.IOException;
import java.util.*;

public class MockSubscriber implements Subscriber {
    @Getter
    private final Map<Class<? extends Message>, Collection<MessageHandler<Message>>> handlers = new HashMap<>();
    @Getter
    private final List<Topic> topics = new LinkedList<>();

    @Override
    public void addTopic(Topic topic) throws IOException {
        topics.add(topic);
    }

    @Override
    public boolean hasTopic(Topic topic) throws IOException {
        return topics.contains(topic);
    }

    @Override
    public void removeTopic(Topic topic) throws IOException {
        topics.remove(topic);
    }

    @Override
    public void addHandler(Class<? extends Message> messageClass, MessageHandler handler) {
        Collection<MessageHandler<Message>> handlers = this.handlers.computeIfAbsent(messageClass, k -> new HashSet<>());
        handlers.add(handler);
    }

    @Override
    public void addHandlerIfNotExists(Class<? extends Message> messageClass, MessageHandler handler) {
        addHandler(messageClass, handler);
    }

    @Override
    public void removeHandler(Class<? extends Message> messageClass, MessageHandler handler) {
        Collection<MessageHandler<Message>> handlers = this.handlers.get(messageClass);
        if (handlers != null) {
            handlers.remove(handler);
        }
    }

    @Override
    public void receive(Message message) {
        Collection<MessageHandler<Message>> handlers = this.handlers.get(message.getClass());

        // Pass the message to every defined handler
        if (handlers != null) {
            for (MessageHandler<Message> handler : handlers) {
                handler.handleMessage(message);
            }
        }
    }

    @Override
    public boolean hasConnection() {
        return true;
    }

    @Override
    public void connect() throws IOException {
    }
}
