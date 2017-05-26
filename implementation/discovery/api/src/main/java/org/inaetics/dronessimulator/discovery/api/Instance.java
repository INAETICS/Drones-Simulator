package org.inaetics.dronessimulator.discovery.api;

import org.inaetics.dronessimulator.discovery.api.discoverynode.Group;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;

import java.util.HashMap;
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

    /**
     * Instantiates a new instance with the given type, group, name and properties. This constructor can be used to
     * build a quick instance. However, it is recommended to subclass this class and use your own constructor together
     * with overriding the setInitialProperties method.
     * @param type The type of this instance.
     * @param group The group of this instance.
     * @param name The name of this instance.
     * @param properties The properties of this instance.
     */
    public Instance(Type type, Group group, String name, Map<String, String> properties) {
        this.type = type;
        this.group = group;
        this.name = name;
        this.properties = properties == null ? new HashMap<>() : properties;

        this.setInitialProperties(properties);
    }

    /**
     * Instantiates a new instance with the given type, group, name and properties.
     * @param type The type of this instance.
     * @param group The group of this instance.
     * @param name The name of this instance.
     */
    public Instance(Type type, Group group, String name) {
        this(type, group, name, null);
    }

    /**
     * Gets the type of this instance.
     * @return The type.
     */
    public Type getType() {
        return this.type;
    }

    /**
     * Gets the group this instance belongs to.
     * @return The group.
     */
    public Group getGroup() {
        return this.group;
    }

    /**
     * Gets the name of this instance.
     * @return The name.
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
     * Sets the initial properties of this instance. This method is called from the constructor and only once. Override
     * this method to add your own properties to the given map.
     * @param properties The properties of this instance.
     */
    protected void setInitialProperties(Map<String, String> properties) {
        // Left blank intentionally.
    }
}
