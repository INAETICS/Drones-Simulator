package org.inaetics.dronessimulator.discovery.api;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Interface for a discoverer which can register and find parts of a system.
 */
public interface Discoverer {
    /**
     * Register a new instance.
     * @param instance The instance to register.
     * @throws DuplicateName Another instance is already registered with this name.
     */
    void register(Instance instance) throws DuplicateName, IOException;

    /**
     * Unregisters the given instance.
     * @param instance The instance to unregister.
     */
    void unregister(Instance instance) throws IOException;

    /**
     * Finds instances by type name. The returned map may be empty.
     * @param type The name of the type of the instances.
     * @return A map containing the group names as keys and a collection of the instance names as values.
     */
    Map<String, Collection<String>> find(String type);

    /**
     * Finds instances by type name and group name. The returned collection may be empty.
     * @param type The name of the type of the instances.
     * @param group The group
     * @return A collection of instance names for the given constraints.
     */
    Collection<String> find(String type, String group);
}
