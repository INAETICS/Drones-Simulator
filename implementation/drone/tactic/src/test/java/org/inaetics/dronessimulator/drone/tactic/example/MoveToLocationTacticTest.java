package org.inaetics.dronessimulator.drone.tactic.example;

import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.components.engine.Engine;
import org.inaetics.dronessimulator.drone.components.gps.GPS;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.drone.tactic.TacticTesterHelper;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashSet;

import static org.mockito.Mockito.mock;

public class MoveToLocationTacticTest {

    @Test
    @Ignore
    public void testComparison() {
        DroneInit drone = new DroneInit();
        drone.setIdentifier("1");
        GPS gps = new GPS(mock(Subscriber.class), drone, new HashSet<>(), null, D3Vector.UNIT, D3Vector.UNIT, D3Vector
                .UNIT, D3PolarCoordinate.UNIT);
        gps.start();


        MoveToLocationTactic tactic = new MoveToLocationTactic();
        tactic.gps = gps;


        final D3Vector[] result = new D3Vector[2];
        tactic.engine = new Engine(mock(Publisher.class), drone, gps, new HashSet<>(), null) {
            @Override
            public void changeAcceleration(D3Vector input_acceleration) {
                super.changeAcceleration(input_acceleration);
                result[0] = input_acceleration;
            }
        };
        tactic.moveToLocation(new D3Vector(5, 5, 5));
        tactic.engine = new Engine(mock(Publisher.class), drone, gps, new HashSet<>(), null) {
            @Override
            public void changeAcceleration(D3Vector input_acceleration) {
                super.changeAcceleration(input_acceleration);
                result[1] = input_acceleration;
            }
        };
        tactic.calculateMovement(new D3Vector(5, 5, 5));
        Assert.assertEquals(result[0], result[1]);
    }

    private void setComponentsForEngine(Engine engine, GPS gps, DroneInit droneInit) {
        try {
            TacticTesterHelper.setField(engine, "m_gps", gps);
            TacticTesterHelper.setField(engine, "m_drone", droneInit);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}