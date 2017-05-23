package org.inaetics.dronessimulator.discovery.api.tree;

import org.inaetics.dronessimulator.discovery.api.DiscoveryPath;
import org.inaetics.dronessimulator.discovery.api.discoveryevent.ChangedValue;
import org.inaetics.dronessimulator.discovery.api.discoveryevent.DiscoveryEvent;
import org.inaetics.dronessimulator.discovery.api.discoveryevent.DiscoveryValueEvent;

import java.util.*;

public class DiscoveryDirNode extends DiscoveryNode {
    public DiscoveryDirNode(String id, DiscoveryPath path) {
        super(id, path);
    }

    @Override
    public void setValue(String newValue) {
        throw new RuntimeException("Should not call setValue on a DiscoveryDirNode! Dirs do not have a value");
    }

    @Override
    public Optional<ChangedValue<String>> setValueWithEvent(String newValue) {
        throw new RuntimeException("Should not call setValueWithEvent on a DiscoveryDirNode! Dirs do not have a value");
    }

    @Override
    public void addChild(DiscoveryNode child) {
        child.setParent(this);
        super.addChild(child);
    }

    public List<DiscoveryEvent> updateTree(DiscoveryStoredNode storedNode) {
        assert storedNode.isDir();
        assert storedNode.getKey().equals(this.getId());

        List<DiscoveryEvent> result = new ArrayList<>();

        List<DiscoveryStoredNode> storedChildren = storedNode.getChildren();
        Map<String, DiscoveryNode> children = this.getChildren();
        Set<String> untouched = new HashSet<>(children.keySet()); // Clone set. Otherwise, new keys get added when new elements are added

        // First add and/or update
        for(DiscoveryStoredNode storedChild : storedChildren) {
            DiscoveryNode child = children.get(storedChild.getKey());

            // Child already existed. Update
            if(child != null) {
                untouched.remove(storedChild.getKey());
            // Child is new! Add
            } else {
                // New child is a dir
                if(storedChild.isDir()) {
                    child = new DiscoveryDirNode(storedChild.getKey(), storedChild.getPath());
                // New child is a key
                } else {
                    child = new DiscoveryValueNode(storedChild.getKey(), storedChild.getPath());
                }

                result.add(this.addChildWithEvent(child));
            }

            //Child is added if needed. Now update regardless
            List<DiscoveryEvent> newEvents = child.updateTree(storedChild);

            result.addAll(newEvents);
        }

        // No remove the untouched
        for(String untouchedKey : untouched) {
            result.addAll(this.removeChildWithEvent(untouchedKey));
        }

        return result;
    }
}
