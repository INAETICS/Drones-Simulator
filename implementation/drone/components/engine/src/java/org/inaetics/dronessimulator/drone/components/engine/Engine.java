package org.inaetics.dronessimulator.drone.components.engine;

import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.MovementMessage;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.components.gps.GPS;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
    private volatile GPS m_gps;

    /**
     * The max velocity of this engine in m/s
     */
    private static final int MAX_VELOCITY = 20;

    /**
     * The max acceleration of this engine in m/s^2
     */
    private static final int MAX_ACCELERATION = 10;

    private List<EngineCallback> callbacks = new LinkedList<>();

    //-- GETTERS
    /**
     * Returns the maximum velocity of the engine
     * @return
     */
    public int getMaxVelocity(){
        return MAX_VELOCITY;
    }

    /**
     * Returns the maximum acceleration of the engine
     * @return
     */
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
        if(input.length() < this.getMaxAcceleration() && input.length() != 0){
            double correctionFactor =  this.getMaxAcceleration() / input.length();
            output = input.scale(correctionFactor);
        }
        return output;
    }


    /**
     * Limits the velocity when the maximum velocity is archieved.
     * @param input acceleration as a D3Vector
     * @return optimized acceleration as a D3Vector
     */
    private D3Vector limit_velocity(D3Vector input){
        D3Vector output = input;
        // Check velocity
        if (m_gps.getVelocity().length() >= this.getMaxVelocity() && m_gps.getVelocity().add(input).length() >= m_gps.getVelocity().length() ){
            output = new D3Vector();
        }
        return output;
    }

    /**
     * Stagnate the acceleration when the velocity is at 90% of the maximum velocity.
     * @param input acceleration as a D3Vector
     * @return optimized acceleration as a D3Vector
     */
    public D3Vector stagnate_acceleration(D3Vector input){
        D3Vector output = input;

        // Change acceleration if velocity is close to the maximum velocity
        if (m_gps.getVelocity().length() >= (this.getMaxVelocity() - (this.getMaxVelocity() * 0.1))) {
            double x = MAX_VELOCITY * 0.1;
            double y = m_gps.getVelocity().length() - MAX_VELOCITY*0.9;
            double factor = (y / x);
            logger.debug("#### factor = " + factor);

            double scaledMaxAcceleration = this.getMaxAcceleration() * factor;
            if (input.length() > scaledMaxAcceleration) {
                input.scale(scaledMaxAcceleration / input.length());
            }
        }
        return output;
    }

    /**
     * Send the new desired acceleration to the game-engine
     * @param input_acceleration The new acceleration for the drone using this component
     */
    public void changeAcceleration(D3Vector input_acceleration){
        D3Vector acceleration = input_acceleration;

        acceleration = this.limit_acceleration(acceleration);
        acceleration = this.limit_velocity(acceleration);
        acceleration = this.stagnate_acceleration(acceleration);

        MovementMessage msg = new MovementMessage();
        msg.setAcceleration(acceleration);
        msg.setIdentifier(m_drone.getIdentifier());

        try {
            m_publisher.send(MessageTopic.MOVEMENTS, msg);
        } catch (IOException e) {
            logger.fatal(e);
        }

        //Run all callbacks
        callbacks.forEach(callback -> callback.run(msg));
    }

    public final void registerCallback(EngineCallback callback) {
        callbacks.add(callback);
    }

    @FunctionalInterface
    public interface EngineCallback {
        void run(MovementMessage movementMessage);
    }
}
