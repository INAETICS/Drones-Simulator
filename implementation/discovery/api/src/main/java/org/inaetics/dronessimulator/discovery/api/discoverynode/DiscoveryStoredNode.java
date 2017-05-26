package org.inaetics.dronessimulator.discovery.api.discoverynode;

import java.util.List;
import java.util.Map;

public abstract class DiscoveryStoredNode {
    public abstract String getId();
    public abstract Map<String, String> getValues();
    public abstract List<DiscoveryStoredNode> getChildren();

    public String toString() {
        String children = this.getChildren().stream().map((e) -> "  " + e.toString().replace("\n", "\n  ")).reduce("", (r, c) -> r + "\n" + c);

        return "StoredNode " + this.getId() + " " + this.getValues() + children;
    }

}
