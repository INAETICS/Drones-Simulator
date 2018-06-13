package org.inaetics.dronessimulator.drone.components.radar;

import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;
import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.common.protocol.KillMessage;
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
import org.inaetics.pubsub.api.pubsub.Subscriber;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The Radar drone component
 */
public class Radar implements Subscriber {
    //OSGi constructor
    public Radar() {
        allEntities = new ConcurrentHashMap<>();
    }

    //Testing constructor
    public Radar(ArchitectureEventController architectureEventController, DroneInit drone, Discoverer discoverer, D3Vector position) {
        this.architectureEventController = architectureEventController;
        this.drone = drone;
        this.discoverer = discoverer;
        this.position = position;
        allEntities = new ConcurrentHashMap<>();
    }

    /**
     * Copy constructor.
     */
    public Radar(Radar copy) {
        this.architectureEventController = copy.architectureEventController;
        this.drone = copy.drone;
        this.discoverer = copy.discoverer;
        this.position = new D3Vector().add(copy.position);
        this.allEntities = new ConcurrentHashMap<>(copy.allEntities);
    }

    /**
     * The range of this radar
     */
    public static final int RADAR_RANGE = 500;
    /**
     * Map of all last known entities and their positions (the first string is the id of the entity, the tuple's string is the team name if applicable and the D3Vector is
     * the location)
     */
    private final Map<String, D3Vector> allEntities;
    /**
     * Reference to Architecture Event Controller bundle
     */
    private volatile ArchitectureEventController architectureEventController;
    /**
     * Reference to Drone Init bundle
     */
    private volatile DroneInit drone;
    /**
     * Reference to Discoverer bundle
     */
    private volatile Discoverer discoverer;
    /**
     * Last known position of this drone
     */
    private volatile D3Vector position;

    public D3Vector getPosition() {
        return position;
    }

    public void setPosition(D3Vector position) {
        this.position = position;
    }

    /**
     * Create the logger
     */
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Radar.class);

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
                .parallelStream().min(Comparator.comparingDouble(e -> e.distance_between(position)));
    }

    /**
    /**
     * Handles a StateMessage by changing the internal state based on this message.
     * or a KillMessage by removing the killed entity from the internal state representation.
     * If the incoming message is not a StateMessage or a KillMessage, does nothing.
     *
     * @param msg the received message
     */
    @Override
    public void receive(Object msg, MultipartCallbacks multipartCallbacks) {
        System.out.println("[GPS] Got message " + msg);
        if (msg instanceof StateMessage) {
            StateMessage stateMessage = (StateMessage) msg;
            if (stateMessage.getIdentifier().equals(this.drone.getIdentifier())) {
                D3Vector pos = stateMessage.getPosition();
                if (pos != null) {
                    setPosition(pos);
                }
            } else if (stateMessage.getType().equals(EntityType.DRONE)) {
                D3Vector pos = stateMessage.getPosition();
                if (pos != null) {
                    allEntities.put(stateMessage.getIdentifier(), pos);
                }
            }
        } else if (msg instanceof KillMessage) {
            KillMessage killMessage = (KillMessage) msg;
            if (killMessage.getEntityType().equals(EntityType.DRONE)) {
                this.allEntities.remove(killMessage.getIdentifier());
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Radar radar = (Radar) o;
        return Objects.equals(allEntities, radar.allEntities) &&
                Objects.equals(architectureEventController, radar.architectureEventController) &&
                Objects.equals(drone, radar.drone) &&
                Objects.equals(discoverer, radar.discoverer) &&
                Objects.equals(position, radar.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allEntities, architectureEventController, drone, discoverer, position);
    }
}
