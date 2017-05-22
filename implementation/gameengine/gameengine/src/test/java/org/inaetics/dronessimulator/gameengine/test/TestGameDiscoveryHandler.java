package org.inaetics.dronessimulator.gameengine.test;


import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.gameengine.DiscoveryHandler;
import org.inaetics.dronessimulator.gameengine.common.state.Drone;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapperService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestGameDiscoveryHandler {
    private MockPhysicsEngineDriver driver;
    private DiscoveryHandler discoveryHandler;
    private IdentifierMapperService id_mapper;

    @Before
    public void init() {
        this.driver = new MockPhysicsEngineDriver(this.id_mapper);
        this.id_mapper = new IdentifierMapperService();
        this.discoveryHandler = new DiscoveryHandler(driver, id_mapper);
    }

    @Test
    public void testNewDrone() {
        this.id_mapper.setMapping(1, "1");
        this.discoveryHandler.newDrone("1", new D3Vector());

        Assert.assertEquals(new Drone(1, new D3Vector(), new D3Vector(), new D3Vector()), this.driver.getAdded());
    }

}
