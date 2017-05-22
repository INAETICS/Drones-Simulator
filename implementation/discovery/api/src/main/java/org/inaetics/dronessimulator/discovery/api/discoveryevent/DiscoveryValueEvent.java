package org.inaetics.dronessimulator.discovery.api.discoveryevent;

public class DiscoveryValueEvent<V> extends DiscoveryEvent {
    private final V value;

    public DiscoveryValueEvent(String key, V value) {
        super(key);
        this.value = value;
    }

    public V getValue() {
        return value;
    }
}
