package org.inaetics.dronessimulator.drone.components.radar;

import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.common.Tuple;
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
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.RemovedNode;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.discovery.api.instances.DroneInstance;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The Radar drone component
 */
public class Radar implements MessageHandler {
    /**
     * The logger
     */
    private static final Logger logger = Logger.getLogger(Radar.class);

    /**
     * Reference to Architecture Event Controller bundle
     */
    private volatile ArchitectureEventController m_architectureEventController;
    /**
     * Reference to Subscriber bundle
     */
    private volatile Subscriber m_subscriber;
    /**
     * Reference to Drone Init bundle
     */
    private volatile DroneInit m_drone;
    private volatile Discoverer m_discoverer;

    /**
     * Last known position of this drone
     */
    @Getter
    @Setter
    private volatile D3Vector position;
    /**
     * Map of all last known entities and their positions
     */
    private final ConcurrentHashMap<String, Tuple<String, D3Vector>> addDrones = new ConcurrentHashMap<>();
    /**
     * The range of this radar
     */
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

            if (path.startsWith(DiscoveryPath.type(Type.DRONE)) && path.isConfigPath()) {
                String protocolId = node.getId();
                this.addDrones.remove(protocolId);
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
                    addDrones.clear();
                }
        );
    }

    /*
     * -- GETTERS
     */

    /**
     * Retrieves all last known entities which are in range of this radar
     *
     * @return The entities in range
     */
    public List<Tuple<String, D3Vector>> getRadar() {
        List<Tuple<String, D3Vector>> results;

        if (position != null) {
            results = addDrones.entrySet()
                    .stream()
                    .map(Map.Entry::getValue)
                    .filter(drone -> position.distance_between(drone.getRight()) <= RADAR_RANGE)
                    .collect(Collectors.toList());
        } else {
            results = Collections.emptyList();
        }

        return results;
    }

    /**
     * Retrieves the nearest target in range
     *
     * @return The nearest entity in range
     */
    public Optional<Tuple<String, D3Vector>> getNearestTarget() {
        return getRadar()
                .stream()
                .filter(drone ->
                        drone.getLeft() == null && this.m_drone.getTeamname() == null ||
                                !drone.getLeft().equals(this.m_drone.getTeamname())
                )
                .sorted(Comparator.comparingDouble(e -> e.getRight().distance_between(position)))
                .findFirst();
    }

    //-- MESSAGEHANDLERS

    /**
     * Handles a recieved message and calls the messagehandlers.
     *
     * @param message The received message.
     */
    public void handleMessage(Message message) {
        if (message instanceof StateMessage) {
            handleStateMessage((StateMessage) message);
        } else if (message instanceof KillMessage) {
            handleKillMessage((KillMessage) message);
        }
    }

    /**
     * Handles a stateMessage
     *
     * @param stateMessage the received stateMessage
     */
    private void handleStateMessage(StateMessage stateMessage){
        if (stateMessage.getIdentifier().equals(this.m_drone.getIdentifier())){
            if (stateMessage.getPosition().isPresent()) {
                this.setPosition(stateMessage.getPosition().get());
            }
        } else {
            if (stateMessage.getPosition().isPresent() && stateMessage.getType().equals(EntityType.DRONE)) {
                Tuple<String, D3Vector> droneModel = new Tuple<>(DroneInstance.getTeamname(m_discoverer, stateMessage.getIdentifier()), stateMessage.getPosition().get());
                this.addDrones.put(stateMessage.getIdentifier(), droneModel);
            }
        }
    }

    /**
     * Handles a killMessage
     *
     * @param killMessage the received killMessage
     */
    private void handleKillMessage(KillMessage killMessage) {
        if (killMessage.getEntityType().equals(EntityType.DRONE)) {
            this.addDrones.remove(killMessage.getIdentifier());
        }
    }

}
