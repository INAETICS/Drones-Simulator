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
