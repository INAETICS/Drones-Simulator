package org.inaetics.dronessimulator.drone;
import org.inaetics.dronessimulator.common.protocol.*;
import org.inaetics.dronessimulator.common.*;
import org.inaetics.dronessimulator.drone.handlers.StateMessageHandler;
import org.inaetics.dronessimulator.pubsub.api.publisher.*;
import org.inaetics.dronessimulator.pubsub.api.subscriber.*;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Represents a drone in the system.
 */
public abstract class Drone{
    private volatile Publisher m_publisher;
    private volatile Subscriber m_subscriber;

    private D3Vector position;
    private D3Vector velocity;
    private D3Vector acceleration;

    private StateMessage state_message;

    /** --- CONSTRUCTOR */
    public Drone() {
        this(new D3Vector());
    }

    public Drone(D3Vector p){
        this.position = p;
        this.velocity = new D3Vector();
        this.acceleration = new D3Vector();
    }

    public void init() {
        try {
            this.m_subscriber.addTopic(MessageTopic.STATEUPDATES);
        } catch (IOException e) {
            System.out.println("IO Exception add Topic");
        }
        this.m_subscriber.addHandler(StateMessage.class, new StateMessageHandler(this));
        //this.calculateTactics();
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

    public StateMessage getStateMessage(){ return state_message; }

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

    public void setStateMessage(StateMessage new_state_message){ state_message = new_state_message; }

    /**
     * -- FUNCTIONS
     * */
    abstract void recalculateAcceleration();

    public void calculateTactics(){
        if (state_message.getPosition().isPresent()) {
            this.setPosition(state_message.getPosition().get());
        }
        if (state_message.getAcceleration().isPresent()) {
            this.setAcceleration(state_message.getAcceleration().get());
        }
        if (state_message.getVelocity().isPresent()) {
            this.setVelocity(state_message.getVelocity().get());
        }
        this.recalculateAcceleration();
        this.sendTactics();
    }

    private synchronized void sendTactics(){
        MovementMessage msg = new MovementMessage();
        msg.setAcceleration(this.getAcceleration());
        try {
            m_publisher.send(MessageTopic.MOVEMENTS, msg);
        } catch (IOException e) {
            System.out.println("Exception");
        }
    }






}