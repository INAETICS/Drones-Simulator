package org.inaetics.dronessimulator.discovery.api.tree;

import java.util.List;

public interface DiscoveryStoredNode {
    String getKey();
    String getValue();
    List<DiscoveryStoredNode> getChildren();

    boolean isDir();

}
