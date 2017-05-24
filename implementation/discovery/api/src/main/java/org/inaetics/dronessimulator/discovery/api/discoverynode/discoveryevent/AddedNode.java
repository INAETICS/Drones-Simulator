package org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent;

import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryNode;

public class AddedNode extends NodeEvent {
    public AddedNode(DiscoveryNode node) {
        super(node);
    }
}
