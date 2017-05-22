package org.inaetics.dronessimulator.drone.components.radar;

import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import javax.swing.text.Position;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * Created by mart on 15-5-17.
 */
public class Radar implements MessageHandler {
    private volatile Subscriber m_subscriber;
    private volatile DroneInit m_drone;

    private volatile D3Vector position;
    private ConcurrentHashMap<String, D3Vector> all_positions = new ConcurrentHashMap<String, D3Vector>();
    private static final int RADAR_RANGE = 100;

    /**
     * FELIX CALLBACKS
     */
    public void start() {
        try {
            this.m_subscriber.addTopic(MessageTopic.STATEUPDATES);
        } catch (IOException e) {
            System.out.println("IO Exception add Topic");
        }
        this.m_subscriber.addHandler(StateMessage.class, this);
    }

    /**
     * -- GETTERS
     */
    public D3Vector getPosition(){
        return position;
    }

    public List<D3Vector> getRadar(){
        return all_positions.entrySet()
                .stream()
                .map(e -> e.getValue())
                .filter(object_position -> position.distance_between(object_position) <= RADAR_RANGE)
                .collect(Collectors.toList());
    }

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
            if (stateMessage.getPosition().isPresent()){
                this.all_positions.put(stateMessage.getIdentifier(), stateMessage.getPosition().get());
            }
        }
    }

    public void handleKillMessage(KillMessage killMessage){
        if(killMessage.getIdentifier().equals(this.m_drone.getIdentifier())){
                this.all_positions.remove(killMessage.getIdentifier());
        }
    }

}
