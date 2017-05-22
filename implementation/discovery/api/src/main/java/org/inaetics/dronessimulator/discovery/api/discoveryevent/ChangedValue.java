package org.inaetics.dronessimulator.discovery.api.discoveryevent;

public class ChangedValue<V> extends DiscoveryValueEvent<V> {
    private final V newValue;

    public ChangedValue(String key, V oldValue, V newValue) {
        super(key, oldValue);
        this.newValue = newValue;
    }

    public V getOldValue() {
        return super.getValue();
    }

    public V getNewValue() {
        return this.newValue;
    }
}
