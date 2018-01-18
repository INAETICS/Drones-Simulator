package org.inaetics.dronessimulator.drone.components.radar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;
import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.discovery.api.DiscoveryPath;
import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.NodeEventHandler;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.RemovedNode;
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
@Log4j
@NoArgsConstructor //OSGi constructor
@AllArgsConstructor //Testing constructor
public class Radar implements MessageHandler<Message> {
    /**
     * The range of this radar
     */
    private static final int RADAR_RANGE = 500;
    /**
     * Map of all last known entities and their positions (the first string is the id of the entity, the tuple's string is the team name if applicable and the D3Vector is
     * the location)
     */
    private final Map<String, D3Vector> allEntities = new ConcurrentHashMap<>();
    /**
     * Reference to Architecture Event Controller bundle
     */
    private volatile ArchitectureEventController architectureEventController;
    /**
     * Reference to Subscriber bundle
     */
    private volatile Subscriber subscriber;
    /**
     * Reference to Drone Init bundle
     */
    private volatile DroneInit drone;
    private volatile Discoverer discoverer;
    /**
     * Last known position of this drone
     */
    @Getter
    @Setter
    private volatile D3Vector position;

    /**
     * Start the Radar (called from Apache Felix). This adds handlers for discovery, architectureEventController and subscribes on stateUpdates in subscriber.
     */
    public void start() {
        List<NodeEventHandler<RemovedNode>> removedNodeHandlers = new ArrayList<>();

        removedNodeHandlers.add((RemovedNode e) -> {
            DiscoveryNode node = e.getNode();
            DiscoveryPath path = node.getPath();

            if (path.startsWith(DiscoveryPath.type(Type.DRONE)) && path.isConfigPath()) {
                String protocolId = node.getId();
                this.allEntities.remove(protocolId);
            }
        });
        this.discoverer.addHandlers(true, Collections.emptyList(), Collections.emptyList(), removedNodeHandlers);
        try {
            this.subscriber.addTopic(MessageTopic.STATEUPDATES);
        } catch (IOException e) {
            log.fatal(e);
        }
        this.subscriber.addHandler(StateMessage.class, this);
        this.subscriber.addHandler(KillMessage.class, this);

        architectureEventController.addHandler(SimulationState.INIT, SimulationAction.CONFIG, SimulationState.CONFIG, (fromState, action, toState) -> allEntities.clear());
    }

    /**
     * Retrieves all last known entities which are in range of this radar
     *
     * @return The locations of the entities in range
     */
    public List<D3Vector> getRadar() {
        List<D3Vector> results;

        if (position != null) {
            results = allEntities.values()
                    .parallelStream()
                    .filter(otherDrone -> position.distance_between(otherDrone) <= RADAR_RANGE)
                    .collect(Collectors.toList());
        } else {
            results = Collections.emptyList();
        }

        return new ArrayList<>(results);
    }

    /**
     * Retrieves the nearest target in range
     *
     * @return The nearest entity in range. Note that this could be a team member.
     */
    public Optional<D3Vector> getNearestTarget() {
        return getRadar()
                .parallelStream()
                .sorted(Comparator.comparingDouble(e -> e.distance_between(position)))
                .findFirst();
    }

    /**
     * Handles a recieved message and calls the appropriate message handler.
     *
     * @param message The received message.
     */
    public void handleMessage(Message message) {
        if (message instanceof StateMessage) {
            handleMessage((StateMessage) message);
        } else if (message instanceof KillMessage) {
            handleMessage((KillMessage) message);
        }
    }

    /**
     * Handles a stateMessage by changing the internal state based on this message.
     *
     * @param stateMessage the received stateMessage
     */
    private void handleMessage(StateMessage stateMessage) {
        if (stateMessage.getIdentifier().equals(this.drone.getIdentifier())) {
            stateMessage.getPosition().ifPresent(this::setPosition);
        } else if (stateMessage.getType().equals(EntityType.DRONE)) {
            stateMessage.getPosition().ifPresent(pos -> this.allEntities.put(stateMessage.getIdentifier(), pos));
        }
    }

    /**
     * Handles a killMessage by removing the killed entity from the internal state representation.
     *
     * @param killMessage the received killMessage
     */
    private void handleMessage(KillMessage killMessage) {
        if (killMessage.getEntityType().equals(EntityType.DRONE)) {
            this.allEntities.remove(killMessage.getIdentifier());
        }
    }

}
