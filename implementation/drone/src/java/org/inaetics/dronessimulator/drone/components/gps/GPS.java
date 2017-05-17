package org.inaetics.dronessimulator.drone.components.gps;

import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.drone.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import java.io.IOException;

public class GPS implements MessageHandler {
    private volatile Subscriber m_subscriber;
    private volatile DroneInit m_drone;

    private volatile D3Vector position;
    private volatile D3Vector velocity;
    private volatile D3Vector acceleration;


    /**
     * FELIX CALLBACKS
     */
    public void start() {
        try {
            this.m_subscriber.addTopic(MessageTopic.STATEUPDATES);
        } catch (IOException e) {
            System.out.println("IO Exception add Topic"); // todo logging
        }
        this.m_subscriber.addHandler(StateMessage.class, this);
    }


    /**
     * -- GETTERS
     */
    public D3Vector getPosition(){
        return position;
    }

    public D3Vector getVelocity(){
        return velocity;
    }

    public D3Vector getAcceleration(){
        return acceleration;
    }

    /**
     * -- SETTERS
     */
    private void setPosition(D3Vector new_position){
        position = new_position;
    }

    private void setVelocity(D3Vector new_velocity){
        velocity = new_velocity;
    }

    protected void setAcceleration(D3Vector new_acceleration){
        acceleration = new_acceleration;
    }

    /**
     * -- MESSAGEHANDLER
     */
    public void handleMessage(Message message) {
        if (message instanceof StateMessage){
            StateMessage stateMessage = (StateMessage) message;
            if (stateMessage.getIdentifier().get().equals(this.m_drone.getIdentifier())){
                if (stateMessage.getPosition().isPresent()) {
                    this.setPosition(stateMessage.getPosition().get());
                }
                if (stateMessage.getAcceleration().isPresent()) {
                    this.setAcceleration(stateMessage.getAcceleration().get());
                }
                if (stateMessage.getVelocity().isPresent()) {
                    this.setVelocity(stateMessage.getVelocity().get());
                }
            }
        }
    }

}
