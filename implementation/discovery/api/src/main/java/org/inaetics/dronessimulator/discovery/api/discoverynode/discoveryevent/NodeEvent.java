package org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent;

import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryNode;

public class NodeEvent {
    private final DiscoveryNode node;

    public NodeEvent(DiscoveryNode node) {
        this.node = node;
    }

    public DiscoveryNode getNode() {
        return node;
    }
}
