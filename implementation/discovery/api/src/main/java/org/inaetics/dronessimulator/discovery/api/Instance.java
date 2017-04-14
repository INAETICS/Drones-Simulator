package org.inaetics.dronessimulator.discovery.api;

import java.util.Map;

/**
 * Interface which describes a part of a system.
 */
public interface Instance {
    /**
     * Gets the name of the type of this instance.
     * @return The name of the type.
     */
    String getType();

    /**
     * Gets the name of the group this instance belongs to.
     * @return The name of the group.
     */
    String getGroup();

    /**
     * Gets the name of this specific instance.
     * @return The name of this instance.
     */
    String getName();

    /**
     * Gets the map containing properties to be set for this instance.
     * @return The properties for this instance.
     */
    Map<String, String> getProperties();

    /**
     * Returns whether this instance should be registered as a discoverable configuration.
     * @return Whether to register this instance as a discoverable configuration.
     */
    boolean isConfigDiscoverable();
}
