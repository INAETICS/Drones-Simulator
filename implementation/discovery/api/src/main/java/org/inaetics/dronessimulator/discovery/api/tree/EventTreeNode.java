package org.inaetics.dronessimulator.discovery.api.tree;

import org.inaetics.dronessimulator.discovery.api.discoveryevent.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventTreeNode<V, N extends EventTreeNode<V, N>> extends TreeNode<V, N> {
    private final List<DiscoveryHandler<AddedNode>> addNodeHandlers;
    private final List<DiscoveryHandler<ChangedValue<V>>> changeValueHandlers;
    private final List<DiscoveryHandler<RemovedNode<V>>> removeNodeHandlers;

    public EventTreeNode(String id, Path path) {
        super(id, path);
        this.addNodeHandlers = new CopyOnWriteArrayList<>();
        this.changeValueHandlers = new CopyOnWriteArrayList<>();
        this.removeNodeHandlers = new CopyOnWriteArrayList<>();
    }

    /**
     * Replays the current state through the handler to bring it up too speed
     * @param addHandler
     */
    public void initializeAddHandler(DiscoveryHandler<AddedNode> addHandler) {
        addHandler.handle(new AddedNode(this.getId(), this.getPath()));

        for(Map.Entry<String, N> e : this.getChildren().entrySet()) {
            e.getValue().initializeAddHandler(addHandler);
        }
    }

    public void addAddNodeHandler(DiscoveryHandler<AddedNode> addHandler) {
        this.addNodeHandlers.add(addHandler);
    }

    public void initializeChangeHandler(DiscoveryHandler<ChangedValue<V>> changedValueHandler) {
        if(this.getValue() != null) {
            changedValueHandler.handle(new ChangedValue<>(this.getId(), this.getPath(), null, this.getValue()));
        }

        for(Map.Entry<String, N> e : this.getChildren().entrySet()) {
            e.getValue().initializeChangeHandler(changedValueHandler);
        }
    }

    public void addChangeValueHandler(DiscoveryHandler<ChangedValue<V>> changeHandler) {
        this.changeValueHandlers.add(changeHandler);
    }

    public void addRemoveNodeHandler(DiscoveryHandler<RemovedNode<V>> removeHandler) {
        this.removeNodeHandlers.add(removeHandler);
    }

    public synchronized Optional<ChangedValue<V>> setValueWithEvent(V newValue) {
        ChangedValue<V> result = null;
        V oldValue = this.getValue();

        if((oldValue != null && !oldValue.equals(newValue)) || (oldValue == null && newValue != null)) {
            super.setValue(newValue);
            ChangedValue<V> changedValue = new ChangedValue<>(this.getId(), this.getPath(), oldValue, newValue);
            result = changedValue;
            this.bubbleValueChangeEvent(changedValue);
        }

        return Optional.ofNullable(result);
    }

    protected void bubbleValueChangeEvent(ChangedValue<V> changedValue) {
        this.callHandlers(this.changeValueHandlers, changedValue);

        if(this.getParent() != null) {
            this.getParent().bubbleValueChangeEvent(changedValue);
        }
    }

    public AddedNode addChildWithEvent(N newChild) {
        this.addChild(newChild);
        AddedNode result = new AddedNode(newChild.getId(), newChild.getPath());

        this.bubbleAddEvent(result);

        return result;
    }

    protected void bubbleAddEvent(AddedNode addNode) {
        this.callHandlers(this.addNodeHandlers, addNode);

        if(this.getParent() != null) {
            this.getParent().bubbleAddEvent(addNode);
        }
    }

    public List<RemovedNode<V>> removeChildWithEvent(String id) {
        List<RemovedNode<V>> removeEvents = new ArrayList<>();

        EventTreeNode<V, N> child = this.getChild(id);

        // First remove children's children if any
        for(EventTreeNode<V, N> grandChild : child.getChildren().values()) {
            removeEvents.addAll(child.removeChildWithEvent(grandChild.getId()));
        }

        this.removeChild(id);
        RemovedNode<V> result = new RemovedNode<>(id, this.getPath(), child.getValue());
        removeEvents.add(result);
        this.bubbleRemoveEvent(result);

        return removeEvents;
    }

    protected void bubbleRemoveEvent(RemovedNode<V> removedNode) {
        this.callHandlers(this.removeNodeHandlers, removedNode);

        if(this.getParent() != null) {
            this.getParent().bubbleRemoveEvent(removedNode);
        }
    }

    private <H> void callHandlers(List<DiscoveryHandler<H>> handlers, H toHandle) {
        handlers.forEach((handler) -> handler.handle(toHandle));
    }
}
