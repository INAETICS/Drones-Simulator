package org.inaetics.dronessimulator.gameengine.test;


import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.gameengine.common.state.Bullet;
import org.inaetics.dronessimulator.gameengine.common.state.Drone;
import org.junit.Assert;
import org.junit.Test;

public class TestGameDamage {

    @Test
    public void testDamage() {
        Drone shooter = new Drone(1, new D3Vector(), new D3Vector(), new D3Vector(), new D3PolarCoordinate());
        Bullet bullet = new Bullet(2, 100, shooter, new D3Vector(), new D3Vector(), new D3Vector(), new D3PolarCoordinate());
        Drone target = new Drone(3, new D3Vector(), new D3Vector(), new D3Vector(), new D3PolarCoordinate());

        target.damage(bullet.getDmg());

        Assert.assertEquals(Drone.DRONE_MAX_HEALTH - 100, target.getHp());
    }
}
