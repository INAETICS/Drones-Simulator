package org.inaetics.dronessimulator.architectureevents;

import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.discovery.api.DiscoveryPath;
import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Group;
import org.inaetics.dronessimulator.discovery.api.discoverynode.NodeEventHandler;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.AddedNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.ChangedValue;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.RemovedNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArchitectureEventControllerService implements ArchitectureEventController {
    private volatile Discoverer m_discovery;

    private SimulationState currentFromState;
    private SimulationAction currentAction;
    private SimulationState currentToState;

    private final Map<LifeCycleStep, List<ArchitectureEventHandler>> handlers = new HashMap<>();

    public void start() {
        List<NodeEventHandler<AddedNode>> addHandlers = new ArrayList<>();
        List<NodeEventHandler<ChangedValue>> changedValueHandlers = new ArrayList<>();
        List<NodeEventHandler<RemovedNode>> removedHandlers = new ArrayList<>();

        changedValueHandlers.add((ChangedValue changedValue) -> {
            DiscoveryNode discoveredNode = changedValue.getNode();
            DiscoveryPath discoveredPath = discoveredNode.getPath();

            if(  discoveredPath.isConfigPath()
              && discoveredPath.startsWith(DiscoveryPath.group(Type.SERVICE, Group.SERVICES))
              && "architecture".equals(discoveredNode.getId())
              && "current_life_cycle".equals(changedValue.getKey())
              ) {
                handleNewState(discoveredNode);
            }
        });


        m_discovery.addHandlers(true, addHandlers, changedValueHandlers, removedHandlers);
    }

    public void handleNewState(DiscoveryNode architectureNode) {
        String currentLifeCycle = architectureNode.getValue("current_life_cycle");

        if(currentLifeCycle != null) {
            String[] lifeCycleParts = currentLifeCycle.split("\\.");

            currentFromState = SimulationState.valueOf(lifeCycleParts[0]);
            currentAction = SimulationAction.valueOf(lifeCycleParts[1]);
            currentToState = SimulationState.valueOf(lifeCycleParts[2]);
        } else {
            currentFromState = currentToState;
            currentAction = SimulationAction.DESTROY;
            currentToState = SimulationState.NOSTATE;
        }

        LifeCycleStep lifeCycleStep = new LifeCycleStep(currentFromState, currentAction, currentToState);

        List<ArchitectureEventHandler> handlers = this.handlers.get(lifeCycleStep);

        if(handlers != null) {
            for(ArchitectureEventHandler handler : handlers) {
                handler.handle(currentFromState, currentAction, currentToState);
            }
        }
    }


    public ArchitectureEventController addHandler(SimulationState fromState, SimulationAction action, SimulationState toState, ArchitectureEventHandler handler) {
        LifeCycleStep lifeCycleStep = new LifeCycleStep(fromState, action, toState);

        handlers.computeIfAbsent(lifeCycleStep, (k) -> new ArrayList<>());
        handlers.get(lifeCycleStep).add(handler);

        return this;
    }
}
