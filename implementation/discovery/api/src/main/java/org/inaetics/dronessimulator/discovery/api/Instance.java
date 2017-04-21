package org.inaetics.dronessimulator.discovery.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Interface which describes a part of a system.
 */
public class Instance {
    private String type;
    private String group;
    private String name;
    private Map<String, String> properties;
    private boolean isConfigDiscoverable;

    public Instance(String type, String group, String name, Map<String, String> properties, boolean isConfigDiscoverable) {
        this.type = type;
        this.group = group;
        this.name = name;
        this.properties = properties;
        this.isConfigDiscoverable = isConfigDiscoverable;
    }

    /**
     * Gets the name of the type of this instance.
     * @return The name of the type.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Gets the name of the group this instance belongs to.
     * @return The name of the group.
     */
    public String getGroup() {
        return this.group;
    }

    /**
     * Gets the name of this specific instance.
     * @return The name of this instance.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the map containing properties to be set for this instance.
     * @return The properties for this instance.
     */
    public Map<String, String> getProperties() {
        return this.properties;
    }

    /**
     * Returns whether this instance should be registered as a discoverable configuration.
     * @return Whether to register this instance as a discoverable configuration.
     */
    public boolean isConfigDiscoverable() {
        return this.isConfigDiscoverable;
    }
}
