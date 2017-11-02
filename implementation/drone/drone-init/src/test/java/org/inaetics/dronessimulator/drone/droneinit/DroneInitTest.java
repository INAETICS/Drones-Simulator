package org.inaetics.dronessimulator.drone.droneinit;


import org.inaetics.dronessimulator.discovery.api.Instance;
import org.inaetics.dronessimulator.discovery.api.instances.DroneInstance;
import org.inaetics.dronessimulator.discovery.api.mocks.MockDiscoverer;
import org.inaetics.dronessimulator.test.hamcrest.matchers.StringMatchesUUIDPattern;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.inaetics.dronessimulator.test.hamcrest.matchers.StringMatchesPattern.matches;
import static org.inaetics.dronessimulator.test.hamcrest.matchers.StringMatchesUUIDPattern.matchesThePatternOfAUUID;

public class DroneInitTest {
    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();
    private MockDiscoverer discoverer;
    private DroneInit drone;
    private String teamname;
    private Instance registeredInstance;
    private String presetIdentifier;

    @Before
    public void setUp() throws Exception {
        discoverer = new MockDiscoverer();
        drone = new DroneInit(discoverer);
        teamname = "testingTeam";
        presetIdentifier = "preset_drone_identifier";
        Map<String, String> props = new HashMap<>();
        props.put("team", teamname);
        registeredInstance = new DroneInstance(presetIdentifier, props);
    }

    @Test
    public void getTeamname() throws Exception {
        Assert.assertEquals("Test the default teamname fallback", "unknown_team", drone.getTeamname());
        environmentVariables.set("DRONE_TEAM", teamname);
        Assert.assertEquals(teamname, drone.getTeamname());
    }

    @Test
    public void initIdentifier() throws Exception {
        //A random identifier
        drone.initIdentifier();
        Assert.assertThat(drone.getIdentifier(), matchesThePatternOfAUUID());

        //Based on the host name
        environmentVariables.set("HOSTNAME", "test_host");
        drone.initIdentifier();
        Assert.assertEquals("test_host", drone.getIdentifier());

        //Based on the computer name
        environmentVariables.set("COMPUTERNAME", "test_computer");
        drone.initIdentifier();
        Assert.assertEquals("test_computer", drone.getIdentifier());

        //Based on the given drone name
        environmentVariables.set("DRONENAME", "test_drone");
        drone.initIdentifier();
        Assert.assertEquals("test_drone", drone.getIdentifier());
    }

    @Test
    public void testRegisterDroneService() throws IOException {
        //Given
        drone.setIdentifier(presetIdentifier);
        environmentVariables.set("DRONE_TEAM", teamname);

        //When
        drone.start();

        //Then
        Assert.assertThat(discoverer.getRegisteredInstances(), hasItem(registeredInstance));
        Assert.assertEquals(registeredInstance, discoverer.getRegisteredInstances().get(0));

        //Check for double named instances
        //When
        drone.start();
        //Then
        Assert.assertThat(discoverer.getRegisteredInstances().get(1).getName(), matches(presetIdentifier + "-" + StringMatchesUUIDPattern.UUID_REGEX));
    }

    @Test
    public void testUnregisterDroneService() throws IOException {
        //Given
        drone.setIdentifier(presetIdentifier);
        environmentVariables.set("DRONE_TEAM", teamname);
        drone.start();
        //When
        drone.stop();

        //Then
        Assert.assertThat(discoverer.getUnregisteredInstances(), hasItem(registeredInstance));

    }
}