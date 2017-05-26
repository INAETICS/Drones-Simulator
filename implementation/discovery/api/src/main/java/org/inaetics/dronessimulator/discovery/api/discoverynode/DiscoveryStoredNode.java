package org.inaetics.dronessimulator.discovery.api.discoverynode;

import java.util.List;
import java.util.Map;

/**
 * Abstract class representing a node in storage. This is used so the discovery API can talk about storage level nodes
 * while specific discovery implementations can implement their own logic to build these kind of objects.
 */
public abstract class DiscoveryStoredNode {
    /**
     * Returns the id of the node.
     * @return The id of the node.
     */
    public abstract String getId();

    /**
     * Returns a map containing the values stored in this node.
     * @return The values stored in this node.
     */
    public abstract Map<String, String> getValues();

    /**
     * Returns a list of the children of this node.
     * @return The children of this node.
     */
    public abstract List<DiscoveryStoredNode> getChildren();

    public String toString() {
        String children = this.getChildren().stream().map((e) -> "  " + e.toString().replace("\n", "\n  ")).reduce("", (r, c) -> r + "\n" + c);

        return "StoredNode " + this.getId() + " " + this.getValues() + children;
    }

}
