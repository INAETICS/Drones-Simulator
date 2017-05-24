package org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent;

import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryNode;

public class RemovedNode extends NodeEvent {
    public RemovedNode(DiscoveryNode node) {
        super(node);
    }
}
