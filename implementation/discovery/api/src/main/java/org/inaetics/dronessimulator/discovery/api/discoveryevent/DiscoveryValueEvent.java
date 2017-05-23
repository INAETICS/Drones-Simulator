package org.inaetics.dronessimulator.discovery.api.discoveryevent;

import org.inaetics.dronessimulator.discovery.api.tree.Path;

public class DiscoveryValueEvent<V> extends DiscoveryEvent {
    private final V value;

    public DiscoveryValueEvent(String key, Path path, V value) {
        super(key, path);
        this.value = value;
    }

    public V getValue() {
        return value;
    }
}
