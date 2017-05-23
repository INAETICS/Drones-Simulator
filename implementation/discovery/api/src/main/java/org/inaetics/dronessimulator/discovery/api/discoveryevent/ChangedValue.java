package org.inaetics.dronessimulator.discovery.api.discoveryevent;

import org.inaetics.dronessimulator.discovery.api.tree.Path;

public class ChangedValue<V> extends DiscoveryValueEvent<V> {
    private final V newValue;

    public ChangedValue(String key, Path path, V oldValue, V newValue) {
        super(key, path, oldValue);
        this.newValue = newValue;
    }

    public V getOldValue() {
        return super.getValue();
    }

    public V getNewValue() {
        return this.newValue;
    }
}
