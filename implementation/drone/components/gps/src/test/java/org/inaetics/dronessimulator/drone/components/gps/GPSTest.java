package org.inaetics.dronessimulator.drone.components.gps;

import org.inaetics.dronessimulator.common.Settings;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;
import org.inaetics.dronessimulator.test.MockSubscriber;
import org.inaetics.dronessimulator.test.TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.inaetics.dronessimulator.test.matchers.D3VectorMatcher.closeTo;
import static org.mockito.Mockito.mock;

@SuppressWarnings("ConstantConditions") //Ignore all the issues with the "get" on optionals
public class GPSTest {
    private GPS gps;
    private DroneInit drone;

    @Before
    public void setUp() throws Exception {
        drone = new DroneInit();
        drone.setIdentifier("1");

        gps = new GPS(drone, null, D3Vector.UNIT, D3Vector.UNIT, D3Vector.UNIT, D3PolarCoordinate.UNIT);
        gps.start();
    }

    @Test
    public void handleMessageNoChange() throws Exception {
        TestUtils.setField(gps, "previousMessage", null);
        StateMessage msg1 = new StateMessage(LocalTime.now().minusSeconds(2));
        msg1.setIdentifier(drone.getIdentifier());
        msg1.setAcceleration(new D3Vector(1, 1, 1));
        msg1.setVelocity(new D3Vector(2, 2, 2));
        msg1.setPosition(new D3Vector(3, 3, 3));
        gps.receive(msg1, null);

        StateMessage msg2 = new StateMessage(LocalTime.now().minusSeconds(1));
        msg2.setIdentifier(drone.getIdentifier());
        msg2.setAcceleration(new D3Vector(1, 1, 1));
        msg2.setVelocity(new D3Vector(2, 2, 2));
        msg2.setPosition(new D3Vector(3, 3, 3));
        gps.receive(msg2, null);

        D3Vector nextAcceleration = msg2.getAcceleration().get();
        D3Vector nextVelocity = nextVelocity(new D3Vector(0, 0, 0).add(nextAcceleration), Settings
                .getTickTime(ChronoUnit.SECONDS), msg2.getVelocity().get());
        D3Vector nextPosition = nextPosition(nextVelocity, Settings.getTickTime(ChronoUnit.SECONDS), msg2.getPosition().get());

        Assert.assertThat(gps.getAcceleration(), closeTo(nextAcceleration, 0.1));
        Assert.assertThat(gps.getVelocity(), closeTo(nextVelocity, 0.1));
        Assert.assertThat(gps.getPosition(), closeTo(nextPosition, 0.1));
    }

    @Test
    public void handleMessageIncreasing() throws Exception {
        TestUtils.setField(gps, "previousMessage", null);
        StateMessage msg1 = new StateMessage(LocalTime.now().minusSeconds(2));
        msg1.setIdentifier(drone.getIdentifier());
        msg1.setAcceleration(new D3Vector(1, 1, 1));
        msg1.setVelocity(new D3Vector(2, 2, 2));
        msg1.setPosition(new D3Vector(3, 3, 3));
        gps.receive(msg1, null);

        StateMessage msg2 = new StateMessage(LocalTime.now().minusSeconds(1));
        msg2.setIdentifier(drone.getIdentifier());
        msg2.setAcceleration(new D3Vector(2, 2, 2));
        msg2.setVelocity(new D3Vector(3, 3, 3));
        msg2.setPosition(new D3Vector(6, 6, 6));
        gps.receive(msg2, null);

        D3Vector nextAcceleration = msg2.getAcceleration().get();
        D3Vector nextVelocity = nextVelocity(new D3Vector(0, 0, 0).add(nextAcceleration), Settings.getTickTime(ChronoUnit.SECONDS), msg2.getVelocity().get());
        D3Vector nextPosition = nextPosition(nextVelocity, Settings.getTickTime(ChronoUnit.SECONDS), msg2.getPosition().get());

        Assert.assertThat(gps.getAcceleration(), closeTo(nextAcceleration, 0.1));
        Assert.assertThat(gps.getVelocity(), closeTo(nextVelocity, 0.1));
        Assert.assertThat(gps.getPosition(), closeTo(nextPosition, 0.1));
    }

    public D3Vector nextVelocity(D3Vector nextAcceleration, double time_in_seconds, D3Vector currentVelocity) {
        return nextAcceleration.scale(time_in_seconds).add(currentVelocity);
    }

    /**
     * Calculates the position of this entity after the given period of the given velocity.
     *
     * @param nextVelocity    The change in position during this time step.
     * @param time_in_seconds How long the time step is in seconds.
     * @return The next position for this entity.
     */
    public D3Vector nextPosition(D3Vector nextVelocity, double time_in_seconds, D3Vector currentPosition) {
        return nextVelocity.scale(time_in_seconds).add(currentPosition);
    }


    @Test
    public void handleMessageDecreasing() throws Exception {
        TestUtils.setField(gps, "previousMessage", null);
        StateMessage msg1 = new StateMessage(LocalTime.now().minusSeconds(2));
        msg1.setIdentifier(drone.getIdentifier());
        msg1.setAcceleration(new D3Vector(1, 1, 1));
        msg1.setVelocity(new D3Vector(2, 2, 2));
        msg1.setPosition(new D3Vector(3, 3, 3));
        gps.receive(msg1, null);

        StateMessage msg2 = new StateMessage(LocalTime.now().minusSeconds(1));
        msg2.setIdentifier(drone.getIdentifier());
        msg2.setAcceleration(new D3Vector(-1, -1, -1));
        msg2.setVelocity(new D3Vector(-2, -2, -2));
        msg2.setPosition(new D3Vector(-3, -3, -3));
        gps.receive(msg2, null);

        D3Vector nextAcceleration = msg2.getAcceleration().get();
        D3Vector nextVelocity = nextVelocity(new D3Vector(0, 0, 0).add(nextAcceleration), Settings
                        .getTickTime(ChronoUnit.SECONDS),
                msg2.getVelocity().get());
        D3Vector nextPosition = nextPosition(nextVelocity, Settings.getTickTime(ChronoUnit.SECONDS), msg2.getPosition().get());

        Assert.assertThat(gps.getAcceleration(), closeTo(nextAcceleration, 0.1));
        Assert.assertThat(gps.getVelocity(), closeTo(nextVelocity, 0.1));
        Assert.assertThat(gps.getPosition(), closeTo(nextPosition, 0.1));
    }

    @Test
    public void handleMessageNoDelay() throws Exception {
        TestUtils.setField(gps, "previousMessage", null);
        StateMessage msg1 = new StateMessage(LocalTime.now().minusSeconds(1));
        msg1.setIdentifier(drone.getIdentifier());
        msg1.setAcceleration(new D3Vector(1, 1, 1));
        msg1.setVelocity(new D3Vector(2, 2, 2));
        msg1.setPosition(new D3Vector(3, 3, 3));
        gps.receive(msg1, null);

        StateMessage msg2 = new StateMessage();
        msg2.setIdentifier(drone.getIdentifier());
        msg2.setAcceleration(new D3Vector(2, 2, 2));
        msg2.setVelocity(new D3Vector(3, 3, 3));
        msg2.setPosition(new D3Vector(6, 6, 6));
        gps.receive(msg2, null);

        //Because there is no delay, we expect to have the state to be updated.
        Assert.assertEquals(new D3Vector(2, 2, 2), gps.getAcceleration());
        Assert.assertEquals(new D3Vector(3, 3, 3), gps.getVelocity());
        Assert.assertEquals(new D3Vector(6, 6, 6), gps.getPosition());
    }

}