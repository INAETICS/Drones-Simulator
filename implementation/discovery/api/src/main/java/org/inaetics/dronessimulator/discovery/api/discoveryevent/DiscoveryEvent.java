package org.inaetics.dronessimulator.discovery.api.discoveryevent;

public class DiscoveryEvent {
    private final String key;

    public DiscoveryEvent(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
