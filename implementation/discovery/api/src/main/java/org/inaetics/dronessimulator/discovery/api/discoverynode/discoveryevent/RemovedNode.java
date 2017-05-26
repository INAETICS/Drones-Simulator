package org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent;

import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryNode;

/**
 * Event which is triggered when a node is removed.
 */
public class RemovedNode extends NodeEvent {
    /**
     * Instantiates a new event.
     * @param node The removed node.
     */
    public RemovedNode(DiscoveryNode node) {
        super(node);
    }
}
