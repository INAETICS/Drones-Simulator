package org.inaetics.dronessimulator.discovery.api.tree;

import org.inaetics.dronessimulator.discovery.api.DiscoveryPath;
import org.inaetics.dronessimulator.discovery.api.discoveryevent.DiscoveryEvent;

import java.util.Collections;
import java.util.List;

public class DiscoveryValueNode extends DiscoveryNode {

    public DiscoveryValueNode(String id, DiscoveryPath path) {
        super(id, path);
    }

    @Override
    public List<DiscoveryEvent> updateTree(DiscoveryStoredNode etcdNode) {
        assert !etcdNode.isDir();
        assert etcdNode.getKey().equals(this.getId());

        return this.setValueWithEvent(etcdNode.getValue())
                   .map(stringDiscoveryValueEvent -> Collections.singletonList((DiscoveryEvent) stringDiscoveryValueEvent))
                   .orElseGet(Collections::emptyList);
    }
}
