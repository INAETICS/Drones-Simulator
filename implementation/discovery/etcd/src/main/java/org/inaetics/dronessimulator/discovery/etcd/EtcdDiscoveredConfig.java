package org.inaetics.dronessimulator.discovery.etcd;

import org.inaetics.dronessimulator.discovery.api.DiscoveredConfig;

import java.util.Map;

/**
 * Implementation of a discovered config for etcd.
 */
public class EtcdDiscoveredConfig implements DiscoveredConfig {
    /** Type of the instance. */
    private String type;

    /** Group of the instance. */
    private String group;

    /** Name of the instance. */
    private String name;

    /** The properties associated with this config. */
    private Map<String, String> properties;

    /**
     * Instantiates a new discovered config implementation.
     * @param type Name of the type of the instance.
     * @param group Name of the group of the instance.
     * @param name Name of the instance.
     * @param properties Properties associated with the instance.
     */
    EtcdDiscoveredConfig(String type, String group, String name, Map<String, String> properties) {
        this.type = type;
        this.group = group;
        this.name = name;
        this.properties = properties;
    }

    public String getType() {
        return this.type;
    }

    public String getGroup() {
        return this.group;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public Map<String, String> getProperties() {
        return this.properties;
    }
}
