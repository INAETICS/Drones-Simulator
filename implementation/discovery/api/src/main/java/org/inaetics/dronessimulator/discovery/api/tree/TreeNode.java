package org.inaetics.dronessimulator.discovery.api.tree;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class TreeNode<K, V, N extends TreeNode<K, V, N, P>, P extends Path<P>> {
    private final String id;
    private volatile P path;
    private volatile N parent;
    private final ConcurrentMap<K, V> values;
    private final ConcurrentMap<String, N> children;

    public TreeNode(String id) {
        this(id, (N) null, (P) null);
    }

    public TreeNode(String id, N parent, P path) {
        this.id = id;
        this.parent = parent;
        this.path = path;
        this.values = new ConcurrentHashMap<>();
        this.children = new ConcurrentHashMap<>();
    }

    public String getId() {
        return id;
    }

    public abstract N getSelf();

    public N getChild(String id) {
        return this.children.get(id);
    }

    public Map<String, N> getChildren() {
        return children;
    }

    public void addChild(N child) {
        child.setParent(this.getSelf());
        this.children.put(child.getId(), child);
    }

    public void removeChild(String id) {
        this.children.remove(id);
    }

    public synchronized Map<K, V> getValues() {
        return this.values;
    }

    public synchronized V getValue(K k) {
        return this.values.get(k);
    }

    public synchronized void setValue(K k, V v) {
        if (v == null) {
            this.values.remove(k);
        } else {
            this.values.put(k, v);
        }
    }

    public boolean hasChildren() {
        return this.children.size() > 0;
    }

    public synchronized N getParent() {
        return parent;
    }

    protected synchronized void setParent(N parent) {
        this.parent = parent;

        P parentPath = parent.getPath();
        this.setPath(parentPath.addSegments(this.getId()));
    }

    public synchronized String toString() {
        String children = this.children.entrySet().stream().map((e) -> "  " + e.getValue().toString()).reduce("", (r, c) -> r + "\n" + c);


        return "Node " + this.id + (this.values != null ? " " + this.values : "") + children;
    }

    public P getPath() {
        return path;
    }

    public void setPath(P path) {
        this.path = path;
    }
}
