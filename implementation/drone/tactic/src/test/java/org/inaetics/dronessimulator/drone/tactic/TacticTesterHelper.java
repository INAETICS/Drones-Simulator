package org.inaetics.dronessimulator.drone.tactic;

import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.drone.components.engine.Engine;
import org.inaetics.dronessimulator.drone.components.gps.GPS;
import org.inaetics.dronessimulator.drone.components.radar.Radar;
import org.inaetics.dronessimulator.drone.components.radio.Radio;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.mockito.Mockito.mock;

public class TacticTesterHelper {
    public static <T extends Tactic> T getTactic(Class<T> tacticClass, Publisher publisher, Subscriber
            subscriber, DroneInit droneInit) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        T tactic = tacticClass.newInstance();
        tactic.gps = new GPS(subscriber, droneInit, new LinkedList<>(), D3Vector.UNIT, D3Vector.UNIT, D3Vector.UNIT,
                D3PolarCoordinate.UNIT);
        tactic.gps.start();
        tactic.engine = new Engine(publisher, droneInit, tactic.gps, new LinkedList<>());
        tactic.radio = new Radio(subscriber, publisher, droneInit, new ConcurrentLinkedQueue<>(), null);
        tactic.radio.start();
        tactic.radar = new Radar(mock(ArchitectureEventController.class), subscriber, droneInit, mock(Discoverer.class),
                D3Vector.UNIT);
        tactic.radar.start();
        TacticTesterHelper.setField(tactic, "m_drone", droneInit);
        return tactic;
    }

    static void setField(Object target, String fieldname, Object value) throws NoSuchFieldException, IllegalAccessException {
        doWithFields(target.getClass(),
                field -> {
                    field.setAccessible(true);
                    field.set(target, value);

                },
                field1 -> field1.getName().equals(fieldname)
        );
    }

    /**
     * Invoke the given callback on all fields in the target class, going up the class hierarchy to get all declared
     * fields.
     *
     * @param aClass - the target class to analyze
     * @param fc     - the callback to invoke for each field
     * @param ff     - the filter that determines the fields to apply the callback to
     */
    private static void doWithFields(Class<?> aClass, FieldCallback fc, FieldFilter ff) throws IllegalAccessException {
        Class<?> i = aClass;
        while (i != null && i != Object.class) {
            for (Field field : i.getDeclaredFields()) {
                if (!field.isSynthetic() && ff.matches(field)) {
                    fc.doWith(field);
                }
            }
            i = i.getSuperclass();
        }
    }

    @FunctionalInterface
    private interface FieldFilter {
        boolean matches(final Field field);
    }

    @FunctionalInterface
    private interface FieldCallback {
        void doWith(final Field field) throws IllegalArgumentException, IllegalAccessException;
    }
}
