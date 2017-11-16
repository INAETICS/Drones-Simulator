package org.inaetics.dronessimulator.discovery.api.instances;

import org.inaetics.dronessimulator.discovery.api.MockDiscoverer;
import org.inaetics.dronessimulator.discovery.api.test.MockDiscoveryStoredNode;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class DroneInstanceTest {
    @Test
    public void getTeamname() throws Exception {
        String id = "";
        Map<String, String> props = new HashMap<>();

        DroneInstance instance = new DroneInstance(id);
        MockDiscoverer discoverer = spy(new MockDiscoverer());
        MockDiscoveryStoredNode node = new MockDiscoveryStoredNode(id, props);
        doReturn(node).when(discoverer).getNode(any());

        Assert.assertEquals(DroneInstance.getTeamname(discoverer, id), null);

        String teamname = "test_team";
        props.put("team", teamname);

        Assert.assertEquals(DroneInstance.getTeamname(discoverer, id), teamname);
    }

}