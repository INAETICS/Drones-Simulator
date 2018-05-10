package org.inaetics.dronessimulator.drone.components.engine;

import org.inaetics.dronessimulator.common.Settings;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.MovementMessage;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.components.gps.GPS;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * The engine component in a drone
 */
public final class Engine {
    //This is the constructor for OSGi
    public Engine() {
    }
    //This is a constructor for test purposes.
    public Engine(Publisher m_publisher, GPS m_gps, DroneInit m_drone, D3Vector lastAcceleration) {
        this.m_publisher = m_publisher;
        this.m_gps = m_gps;
        this.m_drone = m_drone;
        this.lastAcceleration = lastAcceleration;
    }

    private final Set<EngineCallback> callbacks = new HashSet<>();
    /** The Publisher to use for sending messages */
    private volatile Publisher m_publisher;
    private volatile GPS m_gps;
    /** The drone instance that can be used to get information about the current drone */
    private volatile DroneInit m_drone;

    /** The last known acceleration, this might be NULL. */
    private D3Vector lastAcceleration;

    public D3Vector getLastAcceleration() {
        return lastAcceleration;
    }

    /**
     * Create the loggrt
     */
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Engine.class);

    /**
     * Limit the acceleration
     *
     * @param input The acceleration to limit
     * @return The limited acceleration
     */
    public static D3Vector limit_acceleration(D3Vector input) {
        D3Vector output = input;
        // Prevent that the acceleration exceeds te maximum acceleration
        if (input.length() > Settings.MAX_DRONE_ACCELERATION) {
            double correctionFactor = Settings.MAX_DRONE_ACCELERATION / input.length();
            output = input.scale(correctionFactor);
        }
        return output;
    }

    /**
     * Maximizes the acceleration in the same direction
     *
     * @param input The vector to scale to the maximal acceleration value
     * @return The vector in the same direction as input but length == max acceleration value
     */
    public static D3Vector maximize_acceleration(D3Vector input) {
        D3Vector output = input;
        if (input.length() < Settings.MAX_DRONE_ACCELERATION && input.length() != 0) {
            double correctionFactor = Settings.MAX_DRONE_ACCELERATION / input.length();
            output = input.scale(correctionFactor);
        }
        return output;
    }

    /**
     * Limits the velocity when the maximum velocity is achieved.
     *
     * Note that there are four cases:
     * - The current velocity is below the max and the new velocity is also below the max -> do nothing
     * - The current velocity is below the max and the new velocity is above the max -> limit
     * - The current velocity is above the max and the new velocity is also above the max -> limit
     * - The current velocity is above the max and the new velocity is below the max -> do nothing
     *
     * @param input velocity as a D3Vector
     * @return optimized velocity as a D3Vector
     */
    private D3Vector limit_velocity(D3Vector input) {
        D3Vector output = input;
        if (m_gps.getVelocity().length() > Settings.MAX_DRONE_VELOCITY && m_gps.getVelocity().add(input).length() > Settings.MAX_DRONE_VELOCITY) {
            double correctionFactor = Settings.MAX_DRONE_VELOCITY / input.length();
            output = input.scale(correctionFactor);
        }

        return output;
    }

    /**
     * Stagnate the acceleration when the velocity is at 90% of the maximum velocity.
     *
     * @param input acceleration as a D3Vector
     * @return optimized acceleration as a D3Vector
     */
    public D3Vector stagnate_acceleration(D3Vector input) {
        D3Vector output = input;
        // Change acceleration if velocity is close to the maximum velocity
        if (m_gps.getVelocity().length() >= (Settings.MAX_DRONE_VELOCITY * 0.9)) {
            double maxAcceleration = Settings.MAX_DRONE_VELOCITY - m_gps.getVelocity().length();
            if (output.length() > Math.abs(maxAcceleration)) {
                output = output.scale(maxAcceleration / output.length() == 0 ? 1 : output.length());
            }
        }
        return output;
    }

    /**
     * Send the new desired acceleration to the game-engine
     *
     * @param input_acceleration The new acceleration for the drone using this component
     */
    public void changeAcceleration(D3Vector input_acceleration) {
        D3Vector acceleration = input_acceleration;

        acceleration = limit_acceleration(acceleration);
        acceleration = this.limit_velocity(acceleration);

        if (Double.isNaN(acceleration.getX()) || Double.isNaN(acceleration.getY()) || Double.isNaN(acceleration.getZ())) {
            throw new IllegalArgumentException("Acceleration is not a number. Input acceleration: " + input_acceleration.toString() + ", Output acceleration: " +
                    acceleration.toString());
        }

        boolean change = true;
        if (lastAcceleration != null) {
            double diffX = Math.abs(lastAcceleration.getX() - acceleration.getX());
            double diffY = Math.abs(lastAcceleration.getY() - acceleration.getY());
            double diffZ = Math.abs(lastAcceleration.getZ() - acceleration.getZ());
            double diffTot = diffX + diffY + diffZ;
            if (diffTot < 3) {
                change = false;
            }
        }

        if (change) {
            lastAcceleration = acceleration;
            MovementMessage msg = new MovementMessage();
            msg.setAcceleration(acceleration);
            msg.setIdentifier(m_drone.getIdentifier());

            try {
                m_publisher.send(MessageTopic.MOVEMENTS, msg);
                //Run all callbacks
                callbacks.forEach(callback -> callback.run(msg));
            } catch (IOException e) {
                log.fatal(e);
            }
        }
    }

    /**
     * Send the new desired velocity to the game-engine. This method will respect the max acceleration and the max velocity that is defined in the Settings.
     *
     * @param input The new velocity for the drone using this component
     */
    public D3Vector changeVelocity(D3Vector input) {
        D3Vector output = input;
        //Check if we are not accelerating too much (but only if we have MAX_DRONE_ACCELERATION higher than 0, to disable this)
        if (Settings.MAX_DRONE_ACCELERATION > 0 && output.sub(m_gps.getVelocity()).length() > Settings.MAX_DRONE_ACCELERATION) {
            double correctionFactor = Settings.MAX_DRONE_ACCELERATION / output.length();
            output = output.scale(correctionFactor);
        }
        //Check if we are not moving too fast
        if (output.length() > Settings.MAX_DRONE_VELOCITY) {
            double correctionFactor = Settings.MAX_DRONE_VELOCITY / output.length();
            output = output.scale(correctionFactor);

        }

        MovementMessage msg = new MovementMessage();
        msg.setVelocity(output);
        msg.setIdentifier(m_drone.getIdentifier());

        try {
            m_publisher.send(MessageTopic.MOVEMENTS, msg);
            //Run all callbacks
            callbacks.forEach(callback -> callback.run(msg));
        } catch (IOException e) {
            log.fatal(e);
        }

        return output;
    }

    /**
     * Submit a callback-function that is called after each movement update is send. The MovementMessage is a parameter for this callback.
     *
     * @param callback the function to be called
     */
    public final void registerCallback(EngineCallback callback) {
        callbacks.add(callback);
    }

    @FunctionalInterface
    public interface EngineCallback {
        void run(MovementMessage movementMessage);
    }
}
