package org.inaetics.dronessimulator.discovery.api.tree;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Abstract class for a node in a tree.
 *
 * @param <K> Type of the keys.
 * @param <V> Type of the values.
 * @param <N> Type of the current, parent and child nodes.
 * @param <P> Type of the path.
 */
public abstract class TreeNode<K, V, N extends TreeNode<K, V, N, P>, P extends Path<P>> {
    /** The id of this node. */
    private final String id;

    /** The path to this node. */
    private volatile P path;

    /** The parent node. */
    private volatile N parent;

    /** A map containing the keys and values stored in this node. */
    private final ConcurrentMap<K, V> values;

    /** A map containing the children of this node by id. */
    private final ConcurrentMap<String, N> children;

    /**
     * Instantiates a new tree node with the given id.
     * @param id The id of the node.
     */
    public TreeNode(String id) {
        this(id, (N) null, (P) null);
    }

    /**
     * Instantiates a new tree node with the given id, parent and path.
     * @param id The id of the node.
     * @param parent The parent of the node.
     * @param path The path to the node.
     */
    public TreeNode(String id, N parent, P path) {
        this.id = id;
        this.parent = parent;
        this.path = path;
        this.values = new ConcurrentHashMap<>();
        this.children = new ConcurrentHashMap<>();
    }

    /**
     * Returns the id of this node.
     * @return The id of this node.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns this node as its concrete type.
     * @return This node.
     */
    public abstract N getSelf();

    /**
     * Returns the child node of this node with the given id.
     * @param id The id of the child.
     * @return The child node.
     */
    public N getChild(String id) {
        return this.children.get(id);
    }

    /**
     * Returns a map containing the children of this node.
     * @return The children of this node.
     */
    public Map<String, N> getChildren() {
        return children;
    }

    /**
     * Adds the given node as child of this node.
     * @param child The node to add as child.
     */
    public void addChild(N child) {
        child.setParent(this.getSelf());
        this.children.put(child.getId(), child);
    }

    /**
     * Removes the child with the given id from the children of this node.
     * @param id The id of the child to remove.
     */
    public void removeChild(String id) {
        this.children.remove(id);
    }

    /**
     * Returns the values of this node.
     * @return The values of this node.
     */
    public synchronized Map<K, V> getValues() {
        return this.values;
    }

    /**
     * Returns the value for the given key.
     * @param k The key.
     * @return The value.
     */
    public synchronized V getValue(K k) {
        return this.values.get(k);
    }

    /**
     * Sets the given value for the given key.
     * @param k The key.
     * @param v The value.
     */
    public synchronized void setValue(K k, V v) {
        if (v == null) {
            this.values.remove(k);
        } else {
            this.values.put(k, v);
        }
    }

    /**
     * @return Whether this node has any children.
     */
    public boolean hasChildren() {
        return this.children.size() > 0;
    }

    /**
     * Returns the parent node of this node.
     * @return The parent node.
     */
    public synchronized N getParent() {
        return parent;
    }

    /**
     * Set the parent node of this node.
     * @param parent The parent node.
     */
    protected synchronized void setParent(N parent) {
        this.parent = parent;

        P parentPath = parent.getPath();
        this.setPath(parentPath.addSegments(this.getId()));
    }

    public synchronized String toString() {
        String children = this.children.entrySet().stream().map((e) -> "  " + e.getValue().toString().replace("\n", "\n  ")).reduce("", (r, c) -> r + "\n" + c);

        return "Node " + this.id + (this.values != null ? " " + this.values : "") + children;
    }

    /**
     * Returns the path to this node.
     * @return The path to this node.
     */
    public P getPath() {
        return path;
    }

    /**
     * Sets the path to this node.
     * @param path The path to this node.
     */
    public void setPath(P path) {
        this.path = path;
    }
}
