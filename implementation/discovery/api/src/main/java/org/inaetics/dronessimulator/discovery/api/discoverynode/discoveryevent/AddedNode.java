package org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent;

import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryNode;

/**
 * Event which is triggered when a node is added.
 */
public class AddedNode extends NodeEvent {
    /**
     * Instantiates a new event.
     * @param node The added node.
     */
    public AddedNode(DiscoveryNode node) {
        super(node);
    }
}
