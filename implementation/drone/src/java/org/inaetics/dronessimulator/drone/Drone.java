package org.inaetics.dronessimulator.drone;
import org.inaetics.dronessimulator.common.protocol.*;
import org.inaetics.dronessimulator.common.*;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.publisher.*;
import org.inaetics.dronessimulator.pubsub.api.subscriber.*;

import java.io.IOException;

/**
 * Represents a drone in the system.
 */
public abstract class Drone{
    private volatile Publisher m_publisher;
    private volatile Subscriber m_subscriber;

    private D3Vector position;
    private D3Vector velocity;
    private D3Vector acceleration;


    private static final int MAX_DEVIATION_POSTION = 1000;
    private static final int MAX_VELOCITY = 100;
    private static final int MAX_ACCELERATION = 10;
    private static final double RANGE_FACTOR = 0.2;


    /** --- CONSTRUCTOR */
    public Drone() {
        this(new D3Vector());
    }

    public Drone(D3Vector p){
        System.out.println("Drone is CREATED");
        this.position = p;
        this.velocity = new D3Vector();
        this.acceleration = new D3Vector();
    }

    public void init() {
        System.out.println("Start INIT");
        this.m_subscriber.addHandler(StateMessage.class, new StateMessageHandler());
        try {
            this.m_subscriber.addTopic(MessageTopic.STATEUPDATES);
        } catch (IOException e) {
            System.out.println("IO Exception add Topic");
        }
        System.out.println("End INIT");
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
     * -- FUNCTIONS
     * */
    abstract void recalculatAcceleration();


    private void calculateTactics(){
        this.recalculatAcceleration();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        this.sendTactics();
    }

    private synchronized void sendTactics(){
        System.out.println("Prepare Tactics message." + this.getAcceleration());
        MovementMessage msg = new MovementMessage();
        msg.setAcceleration(this.getAcceleration());
        try {
            m_publisher.send(MessageTopic.MOVEMENTS, msg);
        } catch (IOException e) {
            System.out.println("Exception");
        }
        System.out.println("Tactics are send.");
    }






}