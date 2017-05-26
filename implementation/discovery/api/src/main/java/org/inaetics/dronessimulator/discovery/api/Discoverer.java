package org.inaetics.dronessimulator.discovery.api;

import org.inaetics.dronessimulator.discovery.api.discoverynode.NodeEventHandler;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.AddedNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.ChangedValue;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.RemovedNode;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
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
     * Finds instances by type name. Blocks until a change is made, then returns the result. The returned map may be
     * empty.
     * @param type The name of the type of the instances.
     * @return A map containing the group names as keys and a collection of the instance names as values.
     */
    Map<String, Collection<String>> waitFor(String type);

    /**
     * Finds instances by type name and group name. The returned collection may be empty.
     * @param type The name of the type of the instances.
     * @param group The name of the group of the instances.
     * @return A collection of instance names for the given constraints.
     */
    Collection<String> find(String type, String group);

    /**
     * Finds instances by type name and group name. Blocks until a change is made, then returns the result. The returned
     * collection may be empty.
     * @param type The name of the type of the instances.
     * @param group The name of the group of the instances.
     * @return A collection of instance names for the given constraints.
     */
    Collection<String> waitFor(String type, String group);

    /**
     * Returns a map containing the properties for the given instance.
     * @param type The name of the type of the instance.
     * @param group The name of the group of the instance.
     * @param name The name of the instance.
     * @return The properties for the given instance.
     */
    Map<String, String> getProperties(String type, String group, String name);

    /**
     * Adds change handlers to the discoverer.
     * @param replay Whether to send all current data to the handlers. The nodes are sent to the add handlers.
     * @param addHandlers Handlers for handling addition of new nodes.
     * @param changedValueHandlers Handlers for handling changes in nodes.
     * @param removedHandlers Handlers for handling the removal of nodes.
     */
    void addHandlers(boolean replay, List<NodeEventHandler<AddedNode>> addHandlers
                                   , List<NodeEventHandler<ChangedValue>> changedValueHandlers
                                   , List<NodeEventHandler<RemovedNode>> removedHandlers);
}
