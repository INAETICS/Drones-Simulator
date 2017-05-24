package org.inaetics.dronessimulator.discovery.api.test;

import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryStoredNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MockDiscoveryStoredNode extends DiscoveryStoredNode {
    private final String id;
    private final Map<String, String> values;
    private final List<DiscoveryStoredNode> children;

    public MockDiscoveryStoredNode(String key) {
        this(key, new ConcurrentHashMap<>(), new ArrayList<>());
    }

    public MockDiscoveryStoredNode(String id, Map<String, String> values) {
        this(id, values, new ArrayList<>());
    }

    public MockDiscoveryStoredNode(String id, List<DiscoveryStoredNode> children) {
        this(id, new ConcurrentHashMap<>(), children);
    }

    public MockDiscoveryStoredNode(String id, Map<String, String> values, List<DiscoveryStoredNode> children) {
        this.id = id;
        this.values = values;
        this.children = children;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Map<String, String> getValues() {
        return this.values;
    }

    @Override
    public List<DiscoveryStoredNode> getChildren() {
        return this.children;
    }

    public void addChild(DiscoveryStoredNode child) {
        this.children.add(child);
    }
}
