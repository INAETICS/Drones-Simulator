package org.inaetics.dronessimulator.discovery.api.discoveryevent;

import org.inaetics.dronessimulator.discovery.api.tree.Path;

public class RemovedNode<V> extends DiscoveryValueEvent<V> {
    public RemovedNode(String key, Path path, V value) {
        super(key, path, value);
    }
}
