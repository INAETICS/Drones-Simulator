package org.inaetics.dronessimulator.drone.tactic;

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

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.mockito.Mockito.mock;

public class TacticTesterHelper {
    public static <T extends Tactic> T getTactic(Class<T> tacticClass, Publisher publisher, Subscriber
            subscriber, DroneInit droneInit, String... components) throws NoSuchFieldException, IllegalAccessException,
            InstantiationException {
        T tactic = tacticClass.newInstance();
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
        TacticTesterHelper.setField(tactic, "m_drone", droneInit);
        TacticTesterHelper.setField(tactic, "m_architectureEventController", new ArchitectureEventControllerService());
        TacticTesterHelper.setField(tactic, "m_subscriber", subscriber);
        TacticTesterHelper.setField(tactic, "m_discoverer", mock(Discoverer.class));
        return tactic;
    }

    public static void setField(Object target, String fieldname, Object value) throws NoSuchFieldException,
            IllegalAccessException {
        doWithFields(target.getClass(),
                field -> {
                    field.setAccessible(true);
                    field.set(target, value);
                    return Optional.empty();

                },
                field1 -> field1.getName().equals(fieldname)
        );
    }

    public static Object getField(Object target, String fieldname) throws IllegalAccessException,
            NoSuchFieldException {
        Optional<Optional<Object>> result = doWithFields(target.getClass(),
                field -> {
                    field.setAccessible(true);
                    return Optional.ofNullable(field.get(target));

                },
                field1 -> field1.getName().equals(fieldname)
        ).stream().filter(Optional::isPresent).findFirst();
        if (result.isPresent() && result.get().isPresent()) {
            return result.get().get();
        } else {
            return null;
        }
    }

    /**
     * Invoke the given callback on all fields in the target class, going up the class hierarchy to get all declared
     * fields.
     *
     * @param aClass - the target class to analyze
     * @param fc     - the callback to invoke for each field
     * @param ff     - the filter that determines the fields to apply the callback to
     */
    private static List<Optional<Object>> doWithFields(Class<?> aClass, FieldCallback fc, FieldFilter ff) throws
            IllegalAccessException {
        Class<?> i = aClass;
        List<Optional<Object>> results = new LinkedList<>();
        while (i != null && i != Object.class) {
            for (Field field : i.getDeclaredFields()) {
                if (!field.isSynthetic() && ff.matches(field)) {
                    results.add(fc.doWith(field));
                }
            }
            i = i.getSuperclass();
        }
        return results;
    }

    public static <E> Tuple<Publisher, Subscriber> getConnectedMockPubSub() {
        Subscriber subscriber = new Subscriber() {
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
        };
        Publisher publisher = (topic, message) -> subscriber.receive(message);
        return new Tuple<>(publisher, subscriber);
    }

    @FunctionalInterface
    private interface FieldFilter {
        boolean matches(final Field field);
    }

    @FunctionalInterface
    private interface FieldCallback {
        Optional<Object> doWith(final Field field) throws IllegalArgumentException, IllegalAccessException;
    }
}
