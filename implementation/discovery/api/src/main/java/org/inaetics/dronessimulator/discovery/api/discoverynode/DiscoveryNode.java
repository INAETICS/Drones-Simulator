package org.inaetics.dronessimulator.discovery.api.discoverynode;

import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.AddedNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.ChangedValue;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.NodeEvent;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.RemovedNode;
import org.inaetics.dronessimulator.discovery.api.tree.TreeNode;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class DiscoveryNode extends TreeNode<String, String, DiscoveryNode, DiscoveryPath> {
    private final List<NodeEventHandler<AddedNode>> addNodeHandlers;
    private final List<NodeEventHandler<ChangedValue>> changeValueHandlers;
    private final List<NodeEventHandler<RemovedNode>> removeNodeHandlers;

    public DiscoveryNode(String id) {
        this(id, null, null);
    }

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
                child = new DiscoveryNode(storedChild.getId());

                events.add(this.addChildWithEvent(child));
            }

            //Child is added if needed. Now update regardless
            events.addAll(child.updateTree(storedChild));
        }

        // Now remove the untouched
        for(String untouchedKey : untouched) {
            events.addAll(this.removeChildWithEvent(untouchedKey));
        }

        events.addAll(this.updateValues(storedNode));

        return events;
    }

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
     * Replays the current state through the handler to bring it up too speed
     * @param addHandler
     */
    public void initializeAddHandler(NodeEventHandler<AddedNode> addHandler) {
        addHandler.handle(new AddedNode(this));

        for(Map.Entry<String, DiscoveryNode> e : this.getChildren().entrySet()) {
            e.getValue().initializeAddHandler(addHandler);
        }
    }

    public void addAddNodeHandler(NodeEventHandler<AddedNode> addHandler) {
        this.addNodeHandlers.add(addHandler);
    }

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

    public void addChangeValueHandler(NodeEventHandler<ChangedValue> changeHandler) {
        this.changeValueHandlers.add(changeHandler);
    }

    public void addRemoveNodeHandler(NodeEventHandler<RemovedNode> removeHandler) {
        this.removeNodeHandlers.add(removeHandler);
    }

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

    protected void bubbleValueChangeEvent(ChangedValue changedValue) {
        this.callHandlers(this.changeValueHandlers, changedValue);

        if(this.getParent() != null) {
            this.getParent().bubbleValueChangeEvent(changedValue);
        }
    }

    public synchronized AddedNode addChildWithEvent(DiscoveryNode newChild) {
        // First add child to tree
        this.addChild(newChild);
        AddedNode result = new AddedNode(newChild);

        this.bubbleAddEvent(result);

        return result;
    }

    protected void bubbleAddEvent(AddedNode addNode) {
        this.callHandlers(this.addNodeHandlers, addNode);

        if(this.getParent() != null) {
            this.getParent().bubbleAddEvent(addNode);
        }
    }

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

    protected void bubbleRemoveEvent(RemovedNode removedNode) {
        this.callHandlers(this.removeNodeHandlers, removedNode);

        if(this.getParent() != null) {
            this.getParent().bubbleRemoveEvent(removedNode);
        }
    }

    private <H extends NodeEvent> void callHandlers(List<NodeEventHandler<H>> handlers, H toHandle) {
        handlers.forEach((handler) -> handler.handle(toHandle));
    }
}
