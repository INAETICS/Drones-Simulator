package org.inaetics.dronessimulator.discovery.api.discoverynode;

import org.inaetics.dronessimulator.discovery.api.DiscoveryPath;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.AddedNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.ChangedValue;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.NodeEvent;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.RemovedNode;
import org.inaetics.dronessimulator.discovery.api.tree.TreeNode;
import org.inaetics.dronessimulator.discovery.api.tree.Tuple;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A node used in discovery. A tree of these nodes is built as a structure to keep the configuration and settings in.
 */
public class DiscoveryNode extends TreeNode<String, String, DiscoveryNode, DiscoveryPath> {
    /** List of the handlers for added node events. */
    private final List<NodeEventHandler<AddedNode>> addNodeHandlers;

    /** List of the handlers for changed value events. */
    private final List<NodeEventHandler<ChangedValue>> changeValueHandlers;

    /** List of the handlers for removed node events. */
    private final List<NodeEventHandler<RemovedNode>> removeNodeHandlers;

    /**
     * Instantiates a new node with the given id.
     * @param id The id of the node.
     */
    public DiscoveryNode(String id) {
        this(id, null, null);
    }

    /**
     * Instantiates a new node with the given id, parent and path.
     * @param id The id of the node.
     * @param parent The parent node.
     * @param path The discovery path of this node.
     */
    public DiscoveryNode(String id, DiscoveryNode parent, DiscoveryPath path) {
        super(id, parent, path);

        this.addNodeHandlers = new CopyOnWriteArrayList<>();
        this.changeValueHandlers = new CopyOnWriteArrayList<>();
        this.removeNodeHandlers = new CopyOnWriteArrayList<>();
    }

    @Override
    public DiscoveryNode getSelf() {
        return this;
    }

    @Override
    public void addChild(DiscoveryNode child) {
        child.setParent(this);
        super.addChild(child);
    }

    /**
     * Updates the tree of nodes and build the appropriate events.
     * @param storedNode The storage level node to compare against.
     * @return The list events for the changes.
     */
    public synchronized List<NodeEvent> updateTree(DiscoveryStoredNode storedNode) {
        assert storedNode.getId().equals(this.getId());

        List<NodeEvent> events = new ArrayList<>();

        List<DiscoveryStoredNode> storedChildren = storedNode.getChildren();
        Map<String, DiscoveryNode> children = this.getChildren();
        Set<String> untouched = new HashSet<>(children.keySet()); // Clone set. Otherwise, new keys get added when new elements are added

        // First add and/or update children
        for(DiscoveryStoredNode storedChild : storedChildren) {
            DiscoveryNode child = children.get(storedChild.getId());

            // Child already existed. Update
            if(child != null) {
                untouched.remove(storedChild.getId());
            // Child is new! Add
            } else {
                // Add child will set both AddNode and ChangedValue events
                Tuple<DiscoveryNode, List<NodeEvent>> addResult = this.addStoredNode(storedChild);
                child = addResult.getT1();
                events.addAll(addResult.getT2());
            }

            // Now update child regardless to also add/update directories
            events.addAll(child.updateTree(storedChild));
        }

        // Now remove the untouched
        for(String untouchedKey : untouched) {
            events.addAll(this.removeChildWithEvent(untouchedKey));
        }

        events.addAll(this.updateValues(storedNode));

        return events;
    }

    public Tuple<DiscoveryNode, List<NodeEvent>> addStoredNode(DiscoveryStoredNode storedNode) {
        List<ChangedValue> changedValueEvents = new ArrayList<>();
        List<NodeEvent> events = new ArrayList<>();

        DiscoveryNode newNode = new DiscoveryNode(storedNode.getId());

        // First set new values on node
        for(Map.Entry<String, String> value : storedNode.getValues().entrySet()) {
            changedValueEvents.add(newNode.setValueWithEvent(value.getKey(), value.getValue()).get());
        }

        // Node add as a child and trigger AddedNode event
        events.add(this.addChildWithEvent(newNode));

        // Now trigger ChangedValue events
        for(ChangedValue changedValueEvent : changedValueEvents) {
            this.bubbleValueChangeEvent(changedValueEvent);
        }

        events.addAll(changedValueEvents);

        return new Tuple<>(newNode, events);
    }

    /**
     * Updates the values for this node and build the appropriate events.
     * @param storedNode The storage level node to compare against.
     * @return The list of events for the changes.
     */
    public List<ChangedValue> updateValues(DiscoveryStoredNode storedNode) {
        List<ChangedValue> events = new ArrayList<>();
        Set<String> untouched = new HashSet<>(this.getValues().keySet());
        Map<String, String> values = this.getValues();
        Map<String, String> storedValues = storedNode.getValues();

        // Set all values of storedNode in this
        for(Map.Entry<String, String> e : storedValues.entrySet()) {
            Optional<ChangedValue> event = this.setValueWithEvent(e.getKey(), e.getValue());

            event.ifPresent(events::add);
            untouched.remove(e.getKey());
        }

        // Now remove untouched
        for(String untouchedValueKey : untouched) {
            events.add(this.setValueWithEvent(untouchedValueKey, null).get());
        }

        return events;
    }

    /**
     * Replays the current state through the handler by sending added node events to the given handler for the nodes
     * that are currently present in the tree.
     * @param addHandler The handler to send the events to.
     */
    public void initializeAddHandler(NodeEventHandler<AddedNode> addHandler) {
        addHandler.handle(new AddedNode(this));

        for(Map.Entry<String, DiscoveryNode> e : this.getChildren().entrySet()) {
            e.getValue().initializeAddHandler(addHandler);
        }
    }

    /**
     * Adds a added node handler to this node.
     * @param addHandler The handler to add.
     */
    public void addAddNodeHandler(NodeEventHandler<AddedNode> addHandler) {
        this.addNodeHandlers.add(addHandler);
    }

    /**
     * Replays the current state through the handler by sending changed value events to the given handler for the keys
     * and values that are currently present in this node.
     * @param changedValueHandler The handler to send the events to.
     */
    public void initializeChangeHandler(NodeEventHandler<ChangedValue> changedValueHandler) {
        // First handle all changes for the values of this node
        for(Map.Entry<String, String> e : this.getValues().entrySet()) {
            if(e.getValue() != null) {
                changedValueHandler.handle(new ChangedValue(this, e.getKey(), null, e.getValue()));
            }
        }

        // Now handle all changes for the values of the children
        for(Map.Entry<String, DiscoveryNode> e : this.getChildren().entrySet()) {
            e.getValue().initializeChangeHandler(changedValueHandler);
        }
    }

    /**
     * Adds a changed value handler to this node.
     * @param changeHandler The handler to add.
     */
    public void addChangeValueHandler(NodeEventHandler<ChangedValue> changeHandler) {
        this.changeValueHandlers.add(changeHandler);
    }

    /**
     * Adds a removed node handler to this node.
     * @param removeHandler The handler to add.
     */
    public void addRemoveNodeHandler(NodeEventHandler<RemovedNode> removeHandler) {
        this.removeNodeHandlers.add(removeHandler);
    }

    /**
     * Sets a value on this node and sends the relevant event for the change if the new value is not equal to the old
     * value.
     * @param key The key to change.
     * @param newValue The (new) value for the given key.
     * @return An optional containing the created event, if an event was created.
     */
    public synchronized Optional<ChangedValue> setValueWithEvent(String key, String newValue) {
        ChangedValue event = null;
        String oldValue = this.getValue(key);

        // Key did not exist, value is new
        if(oldValue == null) {
            event = new ChangedValue(this, key, null, newValue);
        // Key already existed and newValue is different from oldValue
        } else if (!oldValue.equals(newValue)) {
            event = new ChangedValue(this, key, oldValue, newValue);
        }

        // Set new value regardless
        this.setValue(key, newValue);

        // Something changed, so notify everyone
        if(event != null) {
            this.bubbleValueChangeEvent(event);
        }

        return Optional.ofNullable(event);
    }

    /**
     * Sends the given changed value event to the changed value event handlers.
     * @param changedValue The event.
     */
    protected void bubbleValueChangeEvent(ChangedValue changedValue) {
        this.callHandlers(this.changeValueHandlers, changedValue);

        if(this.getParent() != null) {
            this.getParent().bubbleValueChangeEvent(changedValue);
        }
    }

    /**
     * Adds a new node to the tree as child of this node and sends an added node event to the relevant handlers.
     * @param newChild The node to add.
     * @return The created event.
     */
    public synchronized AddedNode addChildWithEvent(DiscoveryNode newChild) {
        // First add child to tree
        this.addChild(newChild);
        AddedNode result = new AddedNode(newChild);

        this.bubbleAddEvent(result);

        return result;
    }

    /**
     * Sends the given added node event to the added node event handlers.
     * @param addNode The event.
     */
    protected void bubbleAddEvent(AddedNode addNode) {
        this.callHandlers(this.addNodeHandlers, addNode);

        if(this.getParent() != null) {
            this.getParent().bubbleAddEvent(addNode);
        }
    }

    /**
     * Removes the child node with the given id recursively from the tree and sends removed node events for the removed
     * nodes to the relevant handlers.
     * @param id The id of the node to remove.
     * @return A list of the created events.
     */
    public synchronized List<NodeEvent> removeChildWithEvent(String id) {
        List<NodeEvent> removeEvents = new ArrayList<>();

        DiscoveryNode child = this.getChild(id);

        // First remove children's children if any
        for(DiscoveryNode grandChild : child.getChildren().values()) {
            removeEvents.addAll(child.removeChildWithEvent(grandChild.getId()));
        }

        // Unset values
        for(String key : child.getValues().keySet()) {
            removeEvents.add(child.setValueWithEvent(key, null).get());
        }

        // Now remove child from tree
        this.removeChild(id);


        RemovedNode result = new RemovedNode(child);
        removeEvents.add(result);
        this.bubbleRemoveEvent(result);

        return removeEvents;
    }

    /**
     * Sends the given removed node event to the removed node event handlers.
     * @param removedNode The event.
     */
    protected void bubbleRemoveEvent(RemovedNode removedNode) {
        this.callHandlers(this.removeNodeHandlers, removedNode);

        if (this.getParent() != null) {
            this.getParent().bubbleRemoveEvent(removedNode);
        }
    }

    /**
     * Calls the given handlers with the given event.
     * @param handlers The handlers to call.
     * @param toHandle The event to pass to the handlers.
     */
    private <H extends NodeEvent> void callHandlers(List<NodeEventHandler<H>> handlers, H toHandle) {
        handlers.forEach((handler) -> handler.handle(toHandle));
    }
}
