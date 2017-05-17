package org.inaetics.dronessimulator.drone;
import org.inaetics.dronessimulator.common.protocol.*;
import org.inaetics.dronessimulator.common.*;
import org.inaetics.dronessimulator.drone.components.gps.handlers.StateMessageHandler;
import org.inaetics.dronessimulator.drone.handlers.DroneHandler;
import org.inaetics.dronessimulator.pubsub.api.publisher.*;
import org.inaetics.dronessimulator.pubsub.api.subscriber.*;
import org.inaetics.dronessimulator.drone.components.radar.*;
import org.inaetics.dronessimulator.drone.components.Component;
import java.util.ArrayList;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a drone in the system.
 */
public abstract class Drone{
    private volatile Publisher m_publisher;
    private volatile Subscriber m_subscriber;
    private volatile Radar m_radar;
    private ArrayList m_components = new ArrayList();
    private volatile DroneHandler drone_handler;


    private D3Vector position;
    private D3Vector velocity;
    private D3Vector acceleration;

    private StateMessage state_message;
    private String drone_identifier;

    private final ConcurrentHashMap<String, StateMessage> surrounding_drones;
    private static final int CALCULATE_MS = 50;


    /** --- CONSTRUCTOR */
    public Drone() {
        this(new D3Vector());
    }

    public Drone(D3Vector p){
        this.position = p;
        this.velocity = new D3Vector();
        this.acceleration = new D3Vector();
        this.drone_handler = new DroneHandler(this);
        this.state_message = new StateMessage();
        this.drone_identifier = UUID.randomUUID().toString();
        this.surrounding_drones = new ConcurrentHashMap<>();
    }

    public void init() {
        try {
            this.m_subscriber.addTopic(MessageTopic.STATEUPDATES);
        } catch (IOException e) {
            System.out.println("IO Exception add Topic");
        }
        this.m_subscriber.addHandler(StateMessage.class, new StateMessageHandler(this));
        this.drone_handler.start();
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

    public synchronized StateMessage  getStateMessage(){ return state_message; }

    public String getDroneId() { return drone_identifier; }

    public Radar getRadar() { return m_radar; }

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

    public synchronized void setStateMessage(StateMessage new_state_message){
        synchronized(state_message){
            state_message = new_state_message;
        }
    }


    /**
     * -- FELIX CALLBACKS
     */
    public void addComponent(Component newComponent){

    }

    public void removeComponent(Component oldComponent){

    }

    /**
     * -- FUNCTIONS
     * */
    abstract D3Vector recalculateAcceleration();

    public void processStateMessage(){
        synchronized (state_message) {
            if (getStateMessage().getPosition().isPresent()) {
                this.setPosition(getStateMessage().getPosition().get());
            }
            if (getStateMessage().getAcceleration().isPresent()) {
                this.setAcceleration(getStateMessage().getAcceleration().get());
            }
            if (getStateMessage().getVelocity().isPresent()) {
                this.setVelocity(getStateMessage().getVelocity().get());
            }
        }
    }


    public void calculateTactics(){
        if (this.getStateMessage() != null){
            this.processStateMessage();
            D3Vector new_accelartion = this.recalculateAcceleration();
            this.sendTactics(new_accelartion);
        }
    }

    private synchronized void sendTactics(D3Vector acceleration){
        MovementMessage msg = new MovementMessage();
        msg.setAcceleration(acceleration);
        msg.setIdentifier(this.drone_id);
        try {
            m_publisher.send(MessageTopic.MOVEMENTS, msg);
        } catch (IOException e) {
            System.out.println("Exception");
        }
    }






}