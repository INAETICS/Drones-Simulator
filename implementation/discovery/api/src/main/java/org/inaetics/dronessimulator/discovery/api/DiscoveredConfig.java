package org.inaetics.dronessimulator.discovery.api;

import java.util.Map;

/**
 * Interface for an OSGI service that contains a discovered configuration for a bundle.
 */
public interface DiscoveredConfig {
    /**
     * Returns a map containing the configuration properties for this discovered configuration.
     * @return The configuration properties.
     */
    Map<String, String> getProperties();
}
