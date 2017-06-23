package org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent;

import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryNode;

/**
 * Event for changed values. If a key was added, the old value will be null. If a key was removed, the new value will be
 * null.
 */
public class ChangedValue extends NodeEvent {
    /** The key of the changed value. */
    private final String key;

    /** The old value for the given key. */
    private final String oldValue;

    /** The new value for the given key. */
    private final String newValue;

    /**
     * Instantiates a new changed value event.
     * @param node The node which value changed.
     * @param key The key of the changed value.
     * @param oldValue The old value for the given key.
     * @param newValue The new value for the given key.
     */
    public ChangedValue(DiscoveryNode node, String key, String oldValue, String newValue) {
        super(node);
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * Returns the key which value was changed.
     * @return The key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the old value for the key. Can be null in case the key was added.
     * @return The old value for the key.
     */
    public String getOldValue() {
        return this.oldValue;
    }

    /**
     * Returns the new value of the key. Can be null in case the key was removed.
     * @return The new value for the key.
     */
    public String getNewValue() {
        return this.newValue;
    }

    @Override
    public String toString() {
        return "ChangedValue " + key + " " + oldValue + " -> " + newValue;
    }
}
