package org.inaetics.dronesimulator.common.protocol;

import org.inaetics.dronesimulator.common.D3PoolCoordinate;
import org.inaetics.dronesimulator.common.D3Vector;

import java.util.Optional;

/**
 * Message used to tell the game state about movements.
 */
public class MovementMessage extends ProtocolMessage {
    /** The direction the object is in. */
    private D3PoolCoordinate direction = null;

    /** The acceleration of the object. */
    private D3Vector acceleration = null;

    public Optional<D3PoolCoordinate> getDirection() {
        return Optional.ofNullable(direction);
    }

    public void setDirection(D3PoolCoordinate direction) {
        this.direction = direction;
    }

    public Optional<D3Vector> getAcceleration() {
        return Optional.ofNullable(acceleration);
    }

    public void setAcceleration(D3Vector acceleration) {
        this.acceleration = acceleration;
    }
}
