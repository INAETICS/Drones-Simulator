package org.inaetics.dronessimulator.discovery.api.discoverynode;

import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.NodeEvent;

/**
 * Interface for node event handlers.
 *
 * @param <T> The node event this handler should handle.
 */
 @FunctionalInterface
public interface NodeEventHandler<T extends NodeEvent> {
    /**
     * Handle the given event.
     * @param t The node event to handle.
     */
    void handle(T t);
}
