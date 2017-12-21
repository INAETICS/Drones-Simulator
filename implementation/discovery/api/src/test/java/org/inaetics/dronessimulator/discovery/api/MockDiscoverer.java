package org.inaetics.dronessimulator.discovery.api;

import lombok.Getter;
import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryStoredNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.NodeEventHandler;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.AddedNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.ChangedValue;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.NodeEvent;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.RemovedNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MockDiscoverer implements Discoverer {
    @Getter
    private final List<NodeEventHandler<AddedNode>> addedHandlers = new LinkedList<>();
    @Getter
    private final List<NodeEventHandler<ChangedValue>> changedHandlers = new LinkedList<>();
    @Getter
    private final List<NodeEventHandler<RemovedNode>> removedHandlers = new LinkedList<>();

    private final List<NodeEvent> happenedEvents = new LinkedList<>();

    @Override
    public void register(Instance instance) throws DuplicateName, IOException {
        throw new NotImplementedException();
    }

    @Override
    public void unregister(Instance instance) throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public Instance updateProperties(Instance instance, Map<String, String> properties) throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public void addHandlers(boolean replay, List<NodeEventHandler<AddedNode>> addHandlers, List<NodeEventHandler<ChangedValue>> changedValueHandlers, List<NodeEventHandler<RemovedNode>> removedHandlers) {
        addedHandlers.addAll(addHandlers);
        changedHandlers.addAll(changedValueHandlers);
        this.removedHandlers.addAll(removedHandlers);

        if (replay) {
            for (NodeEvent event : happenedEvents) {
                if (event instanceof AddedNode) {
                    addedHandlers.forEach(eventHandler -> eventHandler.handle((AddedNode) event));
                } else if (event instanceof ChangedValue) {
                    changedHandlers.forEach(eventHandler -> eventHandler.handle((ChangedValue) event));
                } else if (event instanceof RemovedNode) {
                    this.removedHandlers.forEach(eventHandler -> eventHandler.handle((RemovedNode) event));
                }
            }
        }
    }

    @Override
    public DiscoveryStoredNode getNode(Instance instance) {
        throw new NotImplementedException();
    }

    public void addHappenedEvent(NodeEvent event) {
        happenedEvents.add(event);
    }
}
