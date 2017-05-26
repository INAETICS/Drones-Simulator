package org.inaetics.dronessimulator.discovery.api.test;

import org.inaetics.dronessimulator.discovery.api.DiscoveryPath;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Group;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestDiscoveryPath {
    private DiscoveryPath configPath;

    @Before
    public void init() {
        Type droneType = new Type("drone");
        Group dronesGroup = new Group("drones");
        configPath = DiscoveryPath.config(droneType, dronesGroup, "droneTest");
    }

    @Test
    public void testStartWith() {
        Assert.assertTrue(configPath.startsWith(DiscoveryPath.type(new Type("drone"))));
        Assert.assertTrue(configPath.startsWith(DiscoveryPath.group(new Type("drone"), new Group("drones"))));
        Assert.assertTrue(configPath.startsWith(configPath));
        Assert.assertFalse(configPath.startsWith(new DiscoveryPath("drones", "drones", "droneTest", "toomuch")));
        Assert.assertFalse(configPath.startsWith(DiscoveryPath.type(new Type("wrong"))));
        Assert.assertFalse(configPath.startsWith(DiscoveryPath.group(new Type("wrong"), new Group("drones"))));
    }

    @Test
    public void testIsPaths() {
        Assert.assertTrue(configPath.isConfigPath());
        Assert.assertTrue(DiscoveryPath.type(new Type("type")).isTypePath());
        Assert.assertTrue(DiscoveryPath.group(new Type("type"), new Group("group")).isGroupPath());
    }
}
