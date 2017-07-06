package org.inaetics.dronessimulator.drone.components.engine;

import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.MovementMessage;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;

import java.io.IOException;

/**
 * The engine component in a drone
 */
public class Engine {
    /** The logger */
    private final static Logger logger = Logger.getLogger(Engine.class);

    /**
     * The Publisher bundle
     */
    private volatile Publisher m_publisher;

    /**
     * The Drone Init bundle
     */
    private volatile DroneInit m_drone;

    /**
     * The max velocity of this engine in m/s
     */
    private static final int MAX_VELOCITY = 20;

    /**
     * The max acceleration of this engine in m/s^2
     */
    private static final int MAX_ACCELERATION = 10;

    public int getMaxVelocity(){
        return MAX_VELOCITY;
    }

    public int getMaxAcceleration(){
        return MAX_ACCELERATION;
    }

    /**
     * Limit the acceleration
     * @param input The acceleration to limit
     * @return The limited acceleration
     */
    public D3Vector limit_acceleration(D3Vector input){
        D3Vector output = input;
        // Prevent that the acceleration exteeds te maximum acceleration
        if(input.length() > this.getMaxAcceleration()){
            double correctionFactor = this.getMaxAcceleration() / input.length();
            output = input.scale(correctionFactor);
        }
        return output;
    }

    /**
     * Maximizes the acceleration in the same direction
     * @param input The vector to scale to the maximal acceleration value
     * @return The vector in the same direction as input but length == max acceleration value
     */
    public D3Vector maximize_acceleration(D3Vector input){
        D3Vector output = input;
        // Prevent that the acceleration exteeds te maximum acceleration
        if(input.length() < this.getMaxAcceleration()){
            double correctionFactor =  this.getMaxAcceleration() / input.length();
            output = input.scale(correctionFactor);
        }
        return output;
    }

    /**
     * ?
     * @param input
     * @return
     */
    public D3Vector stagnate_acceleration(D3Vector input){
        D3Vector output = input;
        return output;
    }

    /**
     * Send the new desired acceleration to the game-engine
     * @param input_acceleration The new acceleration for the drone using this component
     */
    public void changeAcceleration(D3Vector input_acceleration){
        D3Vector acceleration = input_acceleration;

        acceleration = this.limit_acceleration(acceleration);
        acceleration = this.stagnate_acceleration(acceleration);

        MovementMessage msg = new MovementMessage();
        msg.setAcceleration(acceleration);
        msg.setIdentifier(m_drone.getIdentifier());

        try {
            m_publisher.send(MessageTopic.MOVEMENTS, msg);
        } catch (IOException e) {
            logger.fatal(e);
        }
    }
}
