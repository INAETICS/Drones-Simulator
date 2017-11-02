package org.inaetics.dronessimulator.discovery.api.instances;

import org.inaetics.dronessimulator.discovery.api.mocks.MockDiscoverer;
import org.junit.Assert;
import org.junit.Test;

public class DroneInstanceTest {
    @Test
    public void getTeamname() throws Exception {
        String id = "";
        DroneInstance instance = new DroneInstance(id);
        MockDiscoverer discoverer = new MockDiscoverer();
        Assert.assertEquals(DroneInstance.getTeamname(discoverer, id), null);
    }

}