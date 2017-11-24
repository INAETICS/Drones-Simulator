package org.inaetics.dronessimulator.gameengine.test;


import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.gameengine.common.state.Bullet;
import org.inaetics.dronessimulator.gameengine.common.state.Drone;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.GameStateManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestGameStateManager {
    private GameStateManager gameStateManager;

    @Before
    public void init() {
        this.gameStateManager = new GameStateManager();
    }

    @Test
    public void testAdd() {
        Drone drone = new Drone(1, null, new D3Vector(), new D3Vector(), new D3Vector(), new D3PolarCoordinate(),
                new D3Vector());
        this.gameStateManager.addEntityState(drone);
        this.gameStateManager.addEntityState(new Bullet(2, 100, drone, new D3Vector(), new D3Vector(), new D3Vector(), new D3PolarCoordinate()));

        Assert.assertEquals(EntityType.DRONE, this.gameStateManager.getTypeFor(1));
        Assert.assertEquals(EntityType.BULLET, this.gameStateManager.getTypeFor(2));

        Assert.assertEquals(drone, this.gameStateManager.getById(1));
    }

    @Test
    public void testRemove() {
        Drone drone = new Drone(1, null, new D3Vector(), new D3Vector(), new D3Vector(), new D3PolarCoordinate(),
                new D3Vector());
        this.gameStateManager.addEntityState(drone);

        Assert.assertEquals(drone, this.gameStateManager.getById(1));

        this.gameStateManager.removeState(1);
        Assert.assertEquals(null, this.gameStateManager.getById(1));
        Assert.assertEquals(null, this.gameStateManager.getById(2));
    }
}
