package org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent;

import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryNode;

public class ChangedValue extends NodeEvent {
    private final String key;
    private final String oldValue;
    private final String newValue;

    public ChangedValue(DiscoveryNode node, String key, String oldValue, String newValue) {
        super(node);
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getKey() {
        return key;
    }

    public String getOldValue() {
        return this.oldValue;
    }

    public String getNewValue() {
        return this.newValue;
    }

    @Override
    public String toString() {
        return "ChangedValue " + key + " " + oldValue + " -> " + newValue;
    }
}
