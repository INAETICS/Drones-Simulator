package org.inaetics.dronessimulator.discovery.api.test;


import org.inaetics.dronessimulator.discovery.api.DiscoveryPath;
import org.inaetics.dronessimulator.discovery.api.tree.DiscoveryStoredNode;

import java.util.ArrayList;
import java.util.List;

public class MockDiscoveryStoredNode extends DiscoveryStoredNode {
    private final String key;
    private final String value;
    private final List<DiscoveryStoredNode> children;

    public MockDiscoveryStoredNode(String key) {
        this(key, (String) null);
    }

    public MockDiscoveryStoredNode(String key, String value) {
        this(key, value, new ArrayList<>());
    }

    public MockDiscoveryStoredNode(String key, List<DiscoveryStoredNode> children) {
        this(key, null, children);
    }

    public MockDiscoveryStoredNode(String key, String value, List<DiscoveryStoredNode> children) {
        this.key = key;
        this.value = value;
        this.children = children;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public void addChild(DiscoveryStoredNode child) {
        this.children.add(child);
    }

    @Override
    public List<DiscoveryStoredNode> getChildren() {
        return this.children;
    }

    @Override
    public DiscoveryPath getPath() {
        return null;
    }

    @Override
    public boolean isDir() {
        return (this.children.size() > 0) && (this.value == null);
    }
}
