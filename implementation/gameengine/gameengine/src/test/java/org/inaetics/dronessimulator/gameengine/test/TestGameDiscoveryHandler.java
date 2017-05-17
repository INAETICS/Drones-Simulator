package org.inaetics.dronessimulator.gameengine.test;


import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.gameengine.DiscoveryHandler;
import org.inaetics.dronessimulator.gameengine.common.state.Drone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestGameDiscoveryHandler {
    private MockPhysicsEngineDriver driver;
    private DiscoveryHandler discoveryHandler;

    @Before
    public void init() {
        this.driver = new MockPhysicsEngineDriver();
        this.discoveryHandler = new DiscoveryHandler(driver);
    }

    @Test
    public void testNewDrone() {
        this.discoveryHandler.newDrone(1, new D3Vector());

        Assert.assertEquals(new Drone(1, new D3Vector(), new D3Vector(), new D3Vector()), this.driver.getIncomingAddQueue().poll());
    }

}
