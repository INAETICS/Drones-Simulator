package org.inaetics.dronessimulator.architectureevents;

import lombok.extern.log4j.Log4j;
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

/**
 * Architecture Event controller bundle
 * Listens to discovery for any changes to the current state
 * If a change occurs, calls the registered handlers for the lifecycle step
 */
@Log4j
public class ArchitectureEventControllerService implements ArchitectureEventController {
    /**
     * Reference to Discovery bundle
     */
    private volatile Discoverer discoverer;

    /**
     * The previous state of the architecture
     */
    private SimulationState currentFromState;
    /**
     * The last taken action of the architecture
     */
    private SimulationAction currentAction;
    /**
     * The current state of the architecture
     */
    private SimulationState currentToState;

    /**
     * The registered handlers
     */
    private final Map<LifeCycleStep, List<ArchitectureEventHandler>> handlers = new HashMap<>();

    /**
     * OSGI Constructor
     */
    public ArchitectureEventControllerService() {
    }

    /**
     * Visualisation Constructor
     */
    public ArchitectureEventControllerService(Discoverer discoverer) {
        this.discoverer = discoverer;
    }

    /**
     * Start this bundle
     * Adds a change value handler to discovery for any state changes
     */
    public void start() {
        List<NodeEventHandler<AddedNode>> addHandlers = new ArrayList<>();
        List<NodeEventHandler<ChangedValue>> changedValueHandlers = new ArrayList<>();
        List<NodeEventHandler<RemovedNode>> removedHandlers = new ArrayList<>();

        changedValueHandlers.add((ChangedValue changedValue) -> {
            DiscoveryNode discoveredNode = changedValue.getNode();
            DiscoveryPath discoveredPath = discoveredNode.getPath();

            if (discoveredPath.isConfigPath()
                    && discoveredPath.startsWith(DiscoveryPath.group(Type.SERVICE, Group.SERVICES))
                    && "architecture".equals(discoveredNode.getId())
                    && "current_life_cycle".equals(changedValue.getKey())
                    ) {
                handleNewState(discoveredNode);
            }
        });


        discoverer.addHandlers(true, addHandlers, changedValueHandlers, removedHandlers);
    }

    /**
     * Handles the state change. The architectureNode should contain the new current state
     *
     * @param architectureNode The node containing the new state
     */
    private void handleNewState(DiscoveryNode architectureNode) {
        String currentLifeCycle = architectureNode.getValue("current_life_cycle");

        if (currentLifeCycle != null) {
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

        List<ArchitectureEventHandler> handlersForLifecycleStep = this.handlers.get(lifeCycleStep);

        log.debug("handleNewState with state: " + lifeCycleStep + " handle with " + (handlersForLifecycleStep != null ? handlersForLifecycleStep.size() : "0") + " handlers");

        if (handlersForLifecycleStep != null) {
            for (ArchitectureEventHandler handler : handlersForLifecycleStep) {
                handler.handle(currentFromState, currentAction, currentToState);
            }
        }
    }

    @Override
    public ArchitectureEventController addHandler(SimulationState fromState, SimulationAction action, SimulationState toState, ArchitectureEventHandler handler) {
        LifeCycleStep lifeCycleStep = new LifeCycleStep(fromState, action, toState);

        handlers.computeIfAbsent(lifeCycleStep, (k) -> new ArrayList<>());
        handlers.get(lifeCycleStep).add(handler);

        return this;
    }
}
