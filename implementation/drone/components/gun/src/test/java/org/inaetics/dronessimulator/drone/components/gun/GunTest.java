package org.inaetics.dronessimulator.drone.components.gun;

import org.inaetics.dronessimulator.common.protocol.FireBulletMessage;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.components.gps.GPS;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.test.MockPublisher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.inaetics.dronessimulator.common.protocol.EntityType.BULLET;
import static org.inaetics.dronessimulator.drone.components.gun.Gun.BULLET_SPEED;
import static org.inaetics.dronessimulator.drone.components.gun.Gun.MAX_DISTANCE;
import static org.inaetics.dronessimulator.test.TestUtils.setField;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GunTest {
    private Gun gun;
    private MockPublisher publisher;
    private DroneInit drone;
    private GPS gps;

    @Before
    public void setUp() {
        publisher = new MockPublisher();
        gps = mock(GPS.class);
        when(gps.getPosition()).thenReturn(D3Vector.UNIT);
        drone = new DroneInit();
        gun = new Gun(publisher, drone, gps, System.currentTimeMillis(), System.currentTimeMillis());

    }

    @Test
    public void fireBullet() throws Exception {
        D3PolarCoordinate direction = new D3PolarCoordinate(90, 90, 100);
        gun.fireBullet(direction);
        Assert.assertThat(publisher.getReceivedMessages().size(), is(1));
        FireBulletMessage receivedMessage = (FireBulletMessage) publisher.getReceivedMessages().get(0).getRight();
        Assert.assertThat(receivedMessage.getDamage(), is(20));
        Assert.assertThat(receivedMessage.getFiredById(), is(drone.getIdentifier()));
        Assert.assertThat(receivedMessage.getType(), is(BULLET));
        Assert.assertThat(receivedMessage.getDirection(), is(Optional.of(direction)));
        Assert.assertThat(receivedMessage.getVelocity().get().length(), is(BULLET_SPEED));
        Assert.assertThat(receivedMessage.getPosition(), is(Optional.of(gps.getPosition())));
        //We cannot shoot twice very fast, so the next call should not send a new message
        int sizeReceivedMessages = publisher.getReceivedMessages().size();
        gun.fireBullet(direction);
        Assert.assertEquals(sizeReceivedMessages, publisher.getReceivedMessages().size());
        //Do not shoot too far
        setField(gun, "lastShotAtMs", System.currentTimeMillis() - 1000);
        setField(gun, "nextShotAtMs", System.currentTimeMillis());
        direction = new D3PolarCoordinate(90, 90, MAX_DISTANCE + 10);
        gun.fireBullet(direction);
        Assert.assertEquals(sizeReceivedMessages, publisher.getReceivedMessages().size());
    }

    @Test
    public void testCallbacks() {
        Gun.GunCallback callback = mock(Gun.GunCallback.class);
        gun.registerCallback(callback);
        gun.fireBullet(D3PolarCoordinate.UNIT);
        verify(callback).run(any());
    }
}