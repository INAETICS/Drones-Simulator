package org.inaetics.dronessimulator.drone.components.radar;

import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;
import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    /** Reference to Architecture Event Controller bundle */
    private volatile ArchitectureEventController m_architectureEventController;
    /** Reference to Subscriber bundle */
    private volatile Subscriber m_subscriber;
    /** Reference to Drone Init bundle */
    private volatile DroneInit m_drone;

    /** Last known position of this drone */
    private volatile D3Vector position;
    /** Map of all last known entities and their positions */
    private final ConcurrentHashMap<String, D3Vector> all_positions = new ConcurrentHashMap<>();
    /** The range of this radar */
    private static final int RADAR_RANGE = 500;

    /**
     * FELIX CALLBACKS
     */
    public void start() {
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
    public D3Vector getPosition(){
        return position;
    }

    /**
     * Retrieves all last known entities which are in range of this radar
     * @return The entities in range
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
     * Retrieves the nearest target in range
     * @return The nearest entity in range
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

    /**
     * -- MESSAGEHANDLERS
     */
    public void handleMessage(Message message) {
        if (message instanceof StateMessage){
            handleStateMessage((StateMessage) message);
        } else if (message instanceof KillMessage){
            handleKillMessage((KillMessage) message);
        }
    }

    public void handleStateMessage(StateMessage stateMessage){
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

    public void handleKillMessage(KillMessage killMessage){
        if(killMessage.getEntityType().equals(EntityType.DRONE)) {
            this.all_positions.remove(killMessage.getIdentifier());
        }
    }

}
