package org.inaetics.dronessimulator.drone.components.gps;

import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import java.io.IOException;

public class GPS implements MessageHandler {
    private final static Logger logger = Logger.getLogger(GPS.class);

    private volatile Subscriber m_subscriber;
    private volatile DroneInit m_drone;

    private volatile D3Vector position = new D3Vector();
    private volatile D3Vector velocity = new D3Vector();
    private volatile D3Vector acceleration = new D3Vector();
    private volatile D3PolarCoordinate direction = new D3PolarCoordinate();


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
    }


    // -- GETTERS
    /**
     * Returns the current position of the drone
     * @return the position as a D3Vector
     */
    public D3Vector getPosition(){
        return position;
    }

    /**
     * Returns the current velocity of the drone
     * @return the velocity as a D3Vector
     */
    public D3Vector getVelocity(){
        return velocity;
    }

    /**
     * Returns the current acceleration of the drone
     * @return the acceleration as a D3Vector
     */
    public D3Vector getAcceleration(){
        return acceleration;
    }

    /**
     * Returns the current direction of the drone
     * @return the position as a D3PolarCoordinate
     */
    public D3PolarCoordinate getDirection(){ return direction; }

    // -- SETTERS

    /**
     * Changes the current position of the drone
     * @param new_position the new position as a D3Vector
     */
    private void setPosition(D3Vector new_position){
        position = new_position;
    }

    /**
     * Changes the current velocity of the drone
     * @param new_velocity the new velocity as a D3Vector
     */
    private void setVelocity(D3Vector new_velocity){
        velocity = new_velocity;
    }

    /**
     * Changes the current acceleration of the drone
     * @param new_acceleration the new acceleration as a D3Vector
     */
    protected void setAcceleration(D3Vector new_acceleration){
        acceleration = new_acceleration;
    }

    /**
     * Changes the current direction of the drone
     * @param new_direction the new direction as a D3PolarCoordinate
     */
    private void setDirection(D3PolarCoordinate new_direction) { direction = new_direction; }

    /**
     * -- MESSAGEHANDLER
     */
    public void handleMessage(Message message) {
        if (message instanceof StateMessage){
            StateMessage stateMessage = (StateMessage) message;
            if (stateMessage.getIdentifier().equals(this.m_drone.getIdentifier())){
                if (stateMessage.getPosition().isPresent()) {
                    this.setPosition(stateMessage.getPosition().get());
                }
                if (stateMessage.getAcceleration().isPresent()) {
                    this.setAcceleration(stateMessage.getAcceleration().get());
                }
                if (stateMessage.getVelocity().isPresent()) {
                    this.setVelocity(stateMessage.getVelocity().get());
                }
                if(stateMessage.getDirection().isPresent()){
                    this.setDirection(stateMessage.getDirection().get());
                }
            }
        }
    }

}
