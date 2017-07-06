package org.inaetics.dronessimulator.drone.components.radar;

import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;
import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.discovery.api.DiscoveryPath;
import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.NodeEventHandler;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.ChangedValue;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.RemovedNode;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Radar implements MessageHandler {
    private static final Logger logger = Logger.getLogger(Radar.class);

    private volatile ArchitectureEventController m_architectureEventController;
    private volatile Subscriber m_subscriber;
    private volatile DroneInit m_drone;
    private volatile Discoverer m_discoverer;

    private volatile D3Vector position;
    private final ConcurrentHashMap<String, D3Vector> all_positions = new ConcurrentHashMap<>();
    private static final int RADAR_RANGE = 500;

    /**
     * FELIX CALLBACKS
     */
    /**
     * Adds handlers for discovery, architectureEventController and subscribes on stateUpdates in subscriber.
     */
    public void start() {
        List<NodeEventHandler<RemovedNode>> removedNodeHandlers = new ArrayList<>();

        removedNodeHandlers.add((RemovedNode e) -> {
            DiscoveryNode node = e.getNode();
            DiscoveryPath path = node.getPath();

            if(path.startsWith(DiscoveryPath.type(Type.DRONE)) && path.isConfigPath()) {
                String protocolId = node.getId();
                this.all_positions.remove(protocolId);
            }
        });
        this.m_discoverer.addHandlers(true, Collections.emptyList(), Collections.emptyList(), removedNodeHandlers);
        try {
            this.m_subscriber.addTopic(MessageTopic.STATEUPDATES);
        } catch (IOException e) {
            logger.fatal(e);
        }
        this.m_subscriber.addHandler(StateMessage.class, this);
        this.m_subscriber.addHandler(KillMessage.class, this);

        m_architectureEventController.addHandler(SimulationState.INIT, SimulationAction.CONFIG, SimulationState.CONFIG,
                (SimulationState fromState, SimulationAction action, SimulationState toState) -> {
                    all_positions.clear();
                }
        );
    }

    /**
     * -- GETTERS
     */
    /**
     * Retrieves the radar status. All the locations of the objects in the range of the radar.
     * @return
     */
    public List<D3Vector> getRadar(){
        List<D3Vector> results;

        if (position != null) {
            results = all_positions.entrySet()
                    .stream()
                    .map(e -> e.getValue())
                    .filter(object_position -> position.distance_between(object_position) <= RADAR_RANGE)
                    .collect(Collectors.toList());
        } else {
            results = Collections.emptyList();
        }

        return results;
    }

    /**
     * Retrieves location of the nearest Drone.
     * @return D3Vector of the nearest target
     */
    public Optional<D3Vector> getNearestTarget(){
        return getRadar()
                .stream()
                .sorted((e1, e2) -> Double.compare(e1.distance_between(position), e2.distance_between(position)))
                .findFirst();
    }

    /**
     * -- SETTERS
     */
    private void setPosition(D3Vector new_position){
        position = new_position;
    }

    //-- MESSAGEHANDLERS

    /**
     * Handles a recieved message and calls the messagehandlers.
     * @param message The received message.
     */
    public void handleMessage(Message message) {
        if (message instanceof StateMessage){
            handleStateMessage((StateMessage) message);
        } else if (message instanceof KillMessage){
            handleKillMessage((KillMessage) message);
        }
    }

    /**
     * Handles a stateMessage
     * @param stateMessage the received stateMessage
     */
    private void handleStateMessage(StateMessage stateMessage){
        if (stateMessage.getIdentifier().equals(this.m_drone.getIdentifier())){
            if (stateMessage.getPosition().isPresent()) {
                this.setPosition(stateMessage.getPosition().get());
            }
        } else {
            if (stateMessage.getPosition().isPresent() && stateMessage.getType().equals(EntityType.DRONE)){
                this.all_positions.put(stateMessage.getIdentifier(), stateMessage.getPosition().get());
            }
        }
    }

    /**
     * Handles a killMessage
     * @param killMessage the received killMessage
     */
    private void handleKillMessage(KillMessage killMessage){
        if(killMessage.getEntityType().equals(EntityType.DRONE)) {
            this.all_positions.remove(killMessage.getIdentifier());
        }
    }

}
