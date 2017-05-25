package org.inaetics.dronessimulator.discovery.api.test;

import org.inaetics.dronessimulator.discovery.api.DiscoveryPath;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestDiscoveryPath {
    private DiscoveryPath configPath;

    @Before
    public void init() {
        configPath = DiscoveryPath.config("drone", "drones", "droneTest");
    }

    @Test
    public void testStartWith() {
        Assert.assertTrue(configPath.startsWith(DiscoveryPath.type("drone")));
        Assert.assertTrue(configPath.startsWith(DiscoveryPath.group("drone", "drones")));
        Assert.assertTrue(configPath.startsWith(configPath));
        Assert.assertFalse(configPath.startsWith(new DiscoveryPath("drones", "drones", "droneTest", "toomuch")));
        Assert.assertFalse(configPath.startsWith(DiscoveryPath.type("wrong")));
        Assert.assertFalse(configPath.startsWith(DiscoveryPath.group("wrong", "drones")));
    }

    @Test
    public void testIsPaths() {
        Assert.assertTrue(configPath.isConfigPath());
        Assert.assertTrue(DiscoveryPath.type("type").isTypePath());
        Assert.assertTrue(DiscoveryPath.group("type", "group").isGroupPath());
    }
}
