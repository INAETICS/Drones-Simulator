package org.inaetics.dronessimulator.discovery.etcd;

import org.inaetics.dronessimulator.discovery.api.Instance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class EtcdDiscovererTest {
    private static final String ETCD_URI = "http://localhost:4001/";
    private EtcdDiscoverer discoverer;

    // Test instance
    private Instance instance;

    // Test config discoverable instance
    private Instance rabbitmq;

    @Before
    public void setUp() throws Exception {
        this.discoverer = new EtcdDiscoverer(URI.create(ETCD_URI));

        Map<String, String> instanceProperties = new HashMap<>();
        instanceProperties.put("property", "value");
        this.instance = new Instance("unittest", "discovery", "etcd", instanceProperties, false);

        Map<String, String> rabbitmqProperties = new HashMap<>();
        rabbitmqProperties.put("URI", "amqp://rabbit@localhost:5672/dronessimulator");
        rabbitmqProperties.put("host", "localhost");
        rabbitmqProperties.put("port", "5672");
        rabbitmqProperties.put("user", "rabbitmq");
        rabbitmqProperties.put("vhost", "dronessimulator");
        this.rabbitmq = new Instance("rabbitmq", "broker", "default", rabbitmqProperties, true);
    }

    @After
    public void tearDown() throws Exception {
        this.discoverer.unregisterAll();
    }

    /**
     * Tests registering, fetching and unregistering a normal instance.
     */
    @Test
    public void testInstanceLifecycle() throws Exception {
        // Register instance
        discoverer.register(instance);

        // Test find by type
        Map<String, Collection<String>> ofType = discoverer.find("unittest");
        assertArrayEquals(new String[]{"discovery"}, ofType.keySet().toArray());
        assertArrayEquals(new String[]{"etcd"}, ofType.get("discovery").toArray());

        // Test find by group
        Collection<String> ofGroup = discoverer.find("unittest", "discovery");
        assertArrayEquals(new String[]{"etcd"}, ofGroup.toArray());

        // Test find properties
        Map<String, String> properties = discoverer.getProperties("unittest", "discovery", "etcd");
        assertArrayEquals(new String[]{"property"}, properties.keySet().toArray());
        assertEquals("value", properties.get("property"));

        // Unregister instance
        discoverer.unregister(instance);

        // Test that instance is gone
        assertEquals(0, discoverer.find("unittest", "discovery").size());
    }

    /**
     * Tests registering and getting discoverable configs.
     */
    @Test
    public void testDiscoverableConfig() throws Exception {
        // Register instance
        discoverer.register(rabbitmq);

        // Test config registration
        Collection<String> configs = discoverer.getDiscoverableConfigs(false);
        assertArrayEquals(new String[]{EtcdDiscoverer.buildInstancePath(rabbitmq)}, configs.toArray());
        String[] pathSegments = EtcdDiscoverer.splitInstancePath((String) configs.toArray()[0]);

        // Test getting the properties
        Map<String, String> properties = discoverer.getProperties(pathSegments[0], pathSegments[1], pathSegments[2]);
        assertEquals(this.rabbitmq.getProperties(), properties);

        // Unregister instance
        discoverer.unregister(rabbitmq);

        // Test that instance is gone
        configs = discoverer.getDiscoverableConfigs(false);
        assertArrayEquals(new String[]{}, configs.toArray());
        assertEquals(0, discoverer.find("rabbitmq", "broker").size());
    }
}