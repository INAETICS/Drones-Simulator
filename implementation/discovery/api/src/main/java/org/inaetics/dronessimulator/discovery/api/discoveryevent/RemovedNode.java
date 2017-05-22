package org.inaetics.dronessimulator.discovery.api.discoveryevent;

public class RemovedNode<V> extends DiscoveryValueEvent<V> {
    public RemovedNode(String key, V value) {
        super(key, value);
    }
}
