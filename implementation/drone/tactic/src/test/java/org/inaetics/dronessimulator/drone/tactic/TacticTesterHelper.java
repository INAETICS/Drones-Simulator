package org.inaetics.dronessimulator.drone.tactic;

import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventControllerService;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.drone.components.engine.Engine;
import org.inaetics.dronessimulator.drone.components.gps.GPS;
import org.inaetics.dronessimulator.drone.components.gun.Gun;
import org.inaetics.dronessimulator.drone.components.radar.Radar;
import org.inaetics.dronessimulator.drone.components.radio.Radio;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;
import org.inaetics.dronessimulator.test.TestUtils;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;

public class TacticTesterHelper {
    public static <T extends Tactic> T getTactic(Class<T> tacticClass, Publisher publisher, Subscriber subscriber, Discoverer discoverer, DroneInit droneInit, String...
            components) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        T tactic = tacticClass.newInstance();
        return getTactic(tactic, publisher, subscriber, discoverer, droneInit, components);
    }

    public static <T extends Tactic> T getTactic(T tactic, Publisher publisher, Subscriber
            subscriber, Discoverer discoverer, DroneInit droneInit, String... components) throws NoSuchFieldException, IllegalAccessException {
        List<String> componentList = Arrays.asList(components);
        if (componentList.contains("gps") || components.length == 0) {
            tactic.gps = new GPS(subscriber, droneInit, null, D3Vector.UNIT, D3Vector.UNIT, D3Vector.UNIT, D3PolarCoordinate.UNIT);
            tactic.gps.start();
        }
        if (componentList.contains("engine") || components.length == 0) {
            tactic.engine = new Engine(publisher, tactic.gps, droneInit, null);
        }

        if (componentList.contains("radio") || components.length == 0) {
            tactic.radio = new Radio(subscriber, publisher, droneInit, null);
            tactic.radio.start();
        }
        if (componentList.contains("radar") || components.length == 0) {
            tactic.radar = new Radar(mock(ArchitectureEventController.class), subscriber, droneInit, mock(Discoverer.class),
                    D3Vector.UNIT);
            tactic.radar.start();
        }
        if (componentList.contains("gun") || components.length == 0) {
            tactic.gun = new Gun(publisher, droneInit, tactic.gps, System.currentTimeMillis(), System.currentTimeMillis());
        }
        TestUtils.setField(tactic, "drone", droneInit);
        TestUtils.setField(tactic, "architectureEventController", new ArchitectureEventControllerService());
        TestUtils.setField(tactic, "subscriber", subscriber);
        TestUtils.setField(tactic, "discoverer", discoverer);
        return tactic;
    }

}
