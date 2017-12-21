package org.inaetics.dronessimulator.drone.droneinit;

import org.inaetics.dronessimulator.discovery.api.MockDiscoverer;
import org.inaetics.dronessimulator.discovery.api.instances.DroneInstance;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.inaetics.dronessimulator.test.matchers.IsUUIDMatcher.isUUID;

public class DroneInitTest {
    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();
    private DroneInit drone;
    private MockDiscoverer mockDiscoverer;
    private DroneInstance instance;

    @Before
    public void setUp() throws Exception {
        mockDiscoverer = new MockDiscoverer();
        instance = new DroneInstance("drone_id");
        drone = new DroneInit(instance.getName(), mockDiscoverer, instance);
    }

    @Test
    public void testStart() throws Exception {
        environmentVariables.set("DRONENAME", instance.getName());
        drone.initIdentifier();
        //Note that this is mainly testing "registerDroneService"
        drone.start();
        Assert.assertTrue(mockDiscoverer.getRegisteredInstances().parallelStream().filter(ri -> ri.getName().equals(instance.getName())).count() == 1);
        //Register again to get a different identifier
        drone.start();
        Assert.assertThat(drone.getIdentifier(), startsWith(instance.getName() + "-"));
        Assert.assertThat(mockDiscoverer.getRegisteredInstances().size(), is(2));
    }

    @Test
    public void testStop() throws Exception {
        testStart();
        drone.stop();
        Assert.assertTrue(mockDiscoverer.getRegisteredInstances().parallelStream().filter(ri -> ri.getName().equals(instance.getName())).count() == 1);
        Assert.assertTrue(mockDiscoverer.getRegisteredInstances().get(0).getName().equals(instance.getName()));
    }

    @Test
    public void testGetTeamname() throws Exception {
        Assert.assertEquals("unknown_team", drone.getTeamname());
        environmentVariables.set("DRONE_TEAM", "known_team");
        Assert.assertEquals("known_team", drone.getTeamname());
    }

    @Test
    public void testInitIdentifier() throws Exception {
        DroneInit simpleDrone = new DroneInit();
        Assert.assertThat(simpleDrone.getIdentifier(), isUUID());
        environmentVariables.set("HOSTNAME", "HOSTNAME");
        simpleDrone = new DroneInit();
        Assert.assertThat(simpleDrone.getIdentifier(), is("HOSTNAME"));
        environmentVariables.set("COMPUTERNAME", "COMPUTERNAME");
        simpleDrone = new DroneInit();
        Assert.assertThat(simpleDrone.getIdentifier(), is("COMPUTERNAME"));
        environmentVariables.set("DRONENAME", "DRONENAME");
        simpleDrone = new DroneInit();
        Assert.assertThat(simpleDrone.getIdentifier(), is("DRONENAME"));
    }

}