package org.inaetics.dronessimulator.discovery.api;

import org.inaetics.dronessimulator.discovery.api.discoverynode.Group;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;

import java.util.Map;

/**
 * Interface which describes a part of a system.
 */
public class Instance {
    /** The type of this instance. */
    private Type type;

    /** The group of this instance. */
    private Group group;

    /** The name of this instance. */
    private String name;

    /** The properties this instance has. */
    private Map<String, String> properties;

    /** Whether this instance is a discoverable configuration. */
    private boolean isConfigDiscoverable;

    public Instance(Type type, Group group, String name, Map<String, String> properties, boolean isConfigDiscoverable) {
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
        return this.type.getStr();
    }

    /**
     * Gets the name of the group this instance belongs to.
     * @return The name of the group.
     */
    public String getGroup() {
        return this.group.getStr();
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
