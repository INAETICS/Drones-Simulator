package org.inaetics.dronessimulator.discovery.api.test;

import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.discovery.api.Instance;
import org.inaetics.dronessimulator.discovery.api.instances.ArchitectureInstance;
import org.inaetics.dronessimulator.discovery.api.instances.DroneInstance;
import org.inaetics.dronessimulator.discovery.api.instances.GameEngineInstance;
import org.inaetics.dronessimulator.discovery.api.instances.RabbitInstance;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InstancesTest {

    private Map<String, String> properties;

    @Before
    public void setup() {
        properties = new HashMap<>();
        properties.put("testKey", "testValue");
        properties.put("team", "testTeam");
    }

    @Test
    public void testArchitectureInstance() {
        ArchitectureInstance instance = new ArchitectureInstance();
        Assert.assertEquals("/instances/service/services/architecture", Instance.buildInstancePath(instance));
        instance = new ArchitectureInstance(properties);
        Assert.assertEquals("/instances/service/services/architecture", Instance.buildInstancePath(instance));
        Assert.assertEquals(properties, instance.getProperties());
    }

    @Test
    public void testDroneInstance() {
        String droneId = "random_id";
        DroneInstance instance = new DroneInstance(droneId);
        Assert.assertEquals("/instances/drone/drone/" + droneId, Instance.buildInstancePath(instance));
        instance = new DroneInstance(droneId, properties);
        Assert.assertEquals("/instances/drone/drone/" + droneId, Instance.buildInstancePath(instance));
        Assert.assertEquals(properties, instance.getProperties());
    }

    @Test
    public void testDroneTeamname() {
        String droneId = "randomId";
        Discoverer mockDiscoverer = mock(Discoverer.class);
        when(mockDiscoverer.getNode(new DroneInstance(droneId))).thenReturn(new MockDiscoveryStoredNode(droneId, properties));
        Assert.assertEquals(properties.get("team"), DroneInstance.getTeamname(mockDiscoverer, droneId));
    }

    @Test
    public void testGameInstance() {
        GameEngineInstance instance = new GameEngineInstance();
        Assert.assertEquals("/instances/service/services/gameengine", Instance.buildInstancePath(instance));
    }

    @Test
    public void testRabbitInstance() {
        RabbitInstance instance = new RabbitInstance();
        Assert.assertEquals("/instances/rabbitmq/broker/default", Instance.buildInstancePath(instance));
    }
}
