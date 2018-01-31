package org.inaetics.dronessimulator.common.protocol;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Message used to tell the game state about movements.
 */
@Getter
@Setter
@ToString
public class MovementMessage extends ProtocolMessage {
    /** Indentifier of object */
    private String identifier = null;

    /** The direction the object is in. */
    private D3PolarCoordinate direction = null;

    /** The acceleration of the object. */
    private D3Vector acceleration = null;

    private D3Vector velocity = null;

    public Optional<D3PolarCoordinate> getDirection() {
        return Optional.ofNullable(direction);
    }

    public Optional<D3Vector> getAcceleration() {
        return Optional.ofNullable(acceleration);
    }

    public Optional<D3Vector> getVelocity() {
        return Optional.ofNullable(velocity);
    }

    @Override
    public List<MessageTopic> getTopics() {
        return Collections.singletonList(MessageTopic.MOVEMENTS);
    }
}
