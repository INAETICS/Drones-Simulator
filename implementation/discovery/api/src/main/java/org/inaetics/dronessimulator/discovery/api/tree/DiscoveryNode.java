package org.inaetics.dronessimulator.discovery.api.tree;

import org.inaetics.dronessimulator.discovery.api.discoveryevent.DiscoveryEvent;

import java.util.List;

public abstract class DiscoveryNode extends EventTreeNode<String, DiscoveryNode> {
    public DiscoveryNode(String id) {
        super(id);
    }

    public abstract List<DiscoveryEvent> updateTree(DiscoveryStoredNode etcdNode);
}
