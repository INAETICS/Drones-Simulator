package org.inaetics.dronessimulator.discovery.api.discoveryevent;

import org.inaetics.dronessimulator.discovery.api.tree.Path;

public class DiscoveryEvent {
    private final String key;
    private final Path path;

    public DiscoveryEvent(String key, Path path) {
        this.key = key;
        this.path = path;
    }

    public String getKey() {
        return key;
    }

    public Path getPath() {
        return path;
    }
}
