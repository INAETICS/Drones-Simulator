package org.inaetics.dronessimulator.discovery.api.tree;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TreeNode<V, N extends TreeNode<V, N>> {
    private final String id;
    private volatile N parent;
    private volatile V value;
    private final ConcurrentMap<String, N> children;

    public TreeNode(String id) {
        this(id, null, new ConcurrentHashMap<>());
    }

    public TreeNode(String id, V value) {
        this(id, value, new ConcurrentHashMap<>());
    }

    public TreeNode(String id, V value, ConcurrentMap<String, N> children) {
        this.id = id;
        this.value = value;
        this.children = children;
    }

    public String getId() {
        return id;
    }

    public N getChild(String id) {
        return this.children.get(id);
    }

    public Map<String, N> getChildren() {
        return children;
    }

    public void addChild(N child) {
        this.children.put(child.getId(), child);
    }

    public void removeChild(String id) {
        this.children.remove(id);
    }

    public synchronized V getValue() {
        return this.value;
    }

    public synchronized void setValue(V v) {
        this.value = v;
    }

    public boolean hasChildren() {
        return this.children.size() > 0;
    }

    public synchronized N getParent() {
        return parent;
    }

    public synchronized void setParent(N parent) {
        this.parent = parent;
    }
}
