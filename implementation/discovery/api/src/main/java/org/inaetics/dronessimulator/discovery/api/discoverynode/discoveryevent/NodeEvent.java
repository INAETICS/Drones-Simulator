package org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent;

import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryNode;

/**
 * Generic event for a node.
 */
public class NodeEvent {
    /** The node implicated in the event. */
    private final DiscoveryNode node;

    /**
     * Instantiates a new event.
     * @param node The implicated node.
     */
    public NodeEvent(DiscoveryNode node) {
        this.node = node;
    }

    /**
     * Returns the node implicated in this event.
     * @return The implicated node.
     */
    public DiscoveryNode getNode() {
        return node;
    }
}
