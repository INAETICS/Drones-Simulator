package org.inaetics.dronessimulator.drone.tactic;

import lombok.Getter;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventControllerService;
import org.inaetics.dronessimulator.common.Tuple;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.drone.components.engine.Engine;
import org.inaetics.dronessimulator.drone.components.gps.GPS;
import org.inaetics.dronessimulator.drone.components.radar.Radar;
import org.inaetics.dronessimulator.drone.components.radio.Radio;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.Topic;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;
import org.inaetics.dronessimulator.test.TestUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.mockito.Mockito.mock;

public class TacticTesterHelper {
    public static <T extends Tactic> T getTactic(Class<T> tacticClass, Publisher publisher, Subscriber subscriber, DroneInit droneInit, String... components) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        T tactic = tacticClass.newInstance();
        return getTactic(tactic, publisher, subscriber, droneInit, components);
    }

    public static <T extends Tactic> T getTactic(T tactic, Publisher publisher, Subscriber
            subscriber, DroneInit droneInit, String... components) throws NoSuchFieldException, IllegalAccessException,
            InstantiationException {
        List<String> componentList = Arrays.asList(components);
        if (componentList.contains("gps") || components.length == 0) {
            tactic.gps = new GPS(subscriber, droneInit, null, D3Vector.UNIT, D3Vector.UNIT, D3Vector
                    .UNIT,
                    D3PolarCoordinate.UNIT);
            tactic.gps.start();
        }
        if (componentList.contains("engine") || components.length == 0) {
            tactic.engine = new Engine(publisher, tactic.gps, droneInit, null);
        }

        if (componentList.contains("radio") || components.length == 0) {
            tactic.radio = new Radio(subscriber, publisher, droneInit, new ConcurrentLinkedQueue<>(), null);
            tactic.radio.start();
        }
        if (componentList.contains("radar") || components.length == 0) {
            tactic.radar = new Radar(mock(ArchitectureEventController.class), subscriber, droneInit, mock(Discoverer.class),
                    D3Vector.UNIT);
            tactic.radar.start();
        }
        TestUtils.setField(tactic, "m_drone", droneInit);
        TestUtils.setField(tactic, "m_architectureEventController", new ArchitectureEventControllerService());
        TestUtils.setField(tactic, "m_subscriber", subscriber);
        TestUtils.setField(tactic, "m_discoverer", mock(Discoverer.class));
        return tactic;
    }

    public static <E> Tuple<Publisher, MockSubscriber> getConnectedMockPubSub() {
        MockSubscriber subscriber = new MockSubscriber() {

        };
        Publisher publisher = (topic, message) -> subscriber.receive(message);
        return new Tuple<>(publisher, subscriber);
    }

    public static class MockSubscriber implements Subscriber {
        @Getter
        private final Map<Class<? extends Message>, Collection<MessageHandler<Message>>> handlers = new HashMap<>();

        @Override
        public void addTopic(Topic topic) throws IOException {
        }

        @Override
        public void removeTopic(Topic topic) throws IOException {
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
}
