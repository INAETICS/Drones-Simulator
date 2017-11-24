package org.inaetics.dronessimulator.drone.components.engine;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.common.Settings;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.MovementMessage;
import org.inaetics.dronessimulator.common.protocol.TargetMoveLocationMessage;
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
@Log4j
@NoArgsConstructor //This is the constructor for OSGi
@AllArgsConstructor //This is a constructor for test purposes.
public class Engine {
    /**
     * The Publisher bundle
     */
    private volatile Publisher m_publisher;

    /**
     * The Drone Init bundle
     */
    private volatile DroneInit m_drone;
    private volatile GPS m_gps;

    private Set<EngineCallback> callbacks = new HashSet<>();

    /**
     * Limit the acceleration
     *
     * @param input The acceleration to limit
     * @return The limited acceleration
     */
    public D3Vector limit_acceleration(D3Vector input) {
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
    public D3Vector maximize_acceleration(D3Vector input) {
        D3Vector output = input;
        if (input.length() < Settings.MAX_DRONE_ACCELERATION && input.length() != 0) {
            double correctionFactor = Settings.MAX_DRONE_ACCELERATION / input.length();
            output = input.scale(correctionFactor);
        }
        return output;
    }


    /**
     * Limits the velocity when the maximum velocity is archieved.
     *
     * @param input acceleration as a D3Vector
     * @return optimized acceleration as a D3Vector
     */
    private D3Vector limit_velocity(D3Vector input) {
        D3Vector output = input;
        // Check velocity
        if (m_gps.getVelocity().length() >= Settings.MAX_DRONE_VELOCITY && m_gps.getVelocity().add(input).length() >= m_gps.getVelocity()
                .length()) {
            output = new D3Vector();
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
            if (Math.abs(output.length()) > Math.abs(maxAcceleration)) {
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

        acceleration = this.limit_acceleration(acceleration);
        acceleration = this.limit_velocity(acceleration);
        acceleration = this.stagnate_acceleration(acceleration);

        if (Double.isNaN(acceleration.getX()) || Double.isNaN(acceleration.getY()) || Double.isNaN(acceleration.getZ())) {
            throw new IllegalArgumentException("Acceleration is not a number. Input acceleration: " +
                    "" + input_acceleration.toString() + ", Output acceleration: " + acceleration.toString());
        }

        MovementMessage msg = new MovementMessage();
        msg.setAcceleration(acceleration);
        log.info("Move with acc: " + acceleration.toString());
        msg.setIdentifier(m_drone.getIdentifier());

        try {
            m_publisher.send(MessageTopic.MOVEMENTS, msg);
        } catch (IOException e) {
            log.fatal(e);
        }
    }

    public void moveTo(D3Vector location) {
        TargetMoveLocationMessage msg = new TargetMoveLocationMessage();
        msg.setTargetLocation(location);
        log.info("Move to: " + location.toString());
        msg.setIdentifier(m_drone.getIdentifier());

        try {
            m_publisher.send(MessageTopic.MOVEMENTS, msg);
        } catch (IOException e) {
            log.fatal(e);
        }
    }

    public final void registerCallback(EngineCallback callback) {
        callbacks.add(callback);
    }

    @FunctionalInterface
    public interface EngineCallback {
        void run(MovementMessage movementMessage);
    }
}
