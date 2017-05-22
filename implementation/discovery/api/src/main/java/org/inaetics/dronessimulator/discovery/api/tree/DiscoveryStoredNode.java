package org.inaetics.dronessimulator.discovery.api.tree;

import java.util.List;

public abstract class DiscoveryStoredNode {
    public abstract String getKey();
    public abstract String getValue();
    public abstract List<DiscoveryStoredNode> getChildren();

    public abstract boolean isDir();

    public String toString() {
        String children = this.getChildren().stream().map((e) -> "  " + e.toString()).reduce("", (r, c) -> r + "\n" + c);


        return "Node " + this.getKey() + " " + this.getValue() + children;
    }

}
