package org.inaetics.dronessimulator.discovery.api.discoveryevent;

import org.inaetics.dronessimulator.discovery.api.tree.Path;

public class AddedNode extends DiscoveryEvent {
    public AddedNode(String key, Path path) {
        super(key, path);
    }
}
