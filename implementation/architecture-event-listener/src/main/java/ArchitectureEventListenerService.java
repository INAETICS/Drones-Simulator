import org.inaetics.dronessimulator.common.architecture.Action;
import org.inaetics.dronessimulator.common.architecture.State;
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

public class ArchitectureEventListenerService implements ArchitectureEventListener {
    private volatile Discoverer m_discovery;

    private final Map<State, ArchitectureEventHandler> handlers = new HashMap<>();

    public void start() {
        List<NodeEventHandler<AddedNode>> addHandlers = new ArrayList<>();
        List<NodeEventHandler<ChangedValue>> changedValueHandlers = new ArrayList<>();
        List<NodeEventHandler<RemovedNode>> removedHandlers = new ArrayList<>();


        addHandlers.add((AddedNode addedNode) -> {
            DiscoveryNode discoveredNode = addedNode.getNode();
            DiscoveryPath discoveredPath = discoveredNode.getPath();

            if(  discoveredPath.isConfigPath()
              && discoveredPath.startsWith(DiscoveryPath.group(Type.SERVICE, Group.SERVICES))
              && discoveredNode.getId().equals("architecture")
              ) {
                handleNewState(discoveredNode);
            }
        });

        changedValueHandlers.add((ChangedValue changedValue) -> {
            // TODO Rework to atomic ChangedValuesAtNode
            DiscoveryNode discoveredNode = changedValue.getNode();
            DiscoveryPath discoveredPath = discoveredNode.getPath();

            if(  discoveredPath.isConfigPath()
              && discoveredPath.startsWith(DiscoveryPath.group(Type.SERVICE, Group.SERVICES))
              && discoveredNode.getId().equals("architecture")
              && changedValue.getKey().equals("current_state")
              ) {
                handleNewState(discoveredNode);
            }
        });


        m_discovery.addHandlers(true, addHandlers, changedValueHandlers, removedHandlers);
    }

    public void handleNewState(DiscoveryNode architectureNode) {
        // TODO have to change discovery to allow for atomic ChangedValuesAtNode
        // State fromState = State.valueOf(architectureNode.getValue("previous_state"));
        // Action action = Action.valueOf(architectureNode.getValue("previous_action"));
        State toState = State.valueOf(architectureNode.getValue("current_state"));

        handleNewStateTemp(toState);
    }

    // TODO
    // Remove when ChangedValuesAtNode is integrated
    public void handleNewStateTemp(State newState) {
        ArchitectureEventHandler handler = handlers.get(newState);

        handler.handle(newState);
    }

    public void setHandler(State newState, ArchitectureEventHandler handler) {
        handlers.put(newState, handler);
    }
}
