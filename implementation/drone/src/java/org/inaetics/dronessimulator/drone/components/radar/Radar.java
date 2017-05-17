package org.inaetics.dronessimulator.drone.components.radar;

import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.drone.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import javax.swing.text.Position;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

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

    /**
     * -- SETTERS
     */
    private void setPosition(D3Vector new_position){
        position = new_position;
    }

    /**
     * -- MESSAGEHANDLER
     */
    public void handleMessage(Message message) {
        if (message instanceof StateMessage){
            StateMessage stateMessage = (StateMessage) message;
        }
    }

    public void handleStateMessage(StateMessage stateMessage){
        if (stateMessage.getIdentifier().get().equals(this.m_drone.getIdentifier())){
            if (stateMessage.getPosition().isPresent()) {
                this.setPosition(stateMessage.getPosition().get());
            }
        } else {
            if (stateMessage.getPosition().isPresent()){
                this.all_positions.put(stateMessage.getIdentifier().get(), stateMessage.getPosition().get());
            }
        }
    }

    public void handleKillMessage(KillMessage killMessage){
        if (killMessage.get)

    }

}
