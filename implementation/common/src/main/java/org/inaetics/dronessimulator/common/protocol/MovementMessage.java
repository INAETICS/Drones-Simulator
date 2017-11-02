package org.inaetics.dronessimulator.common.protocol;

import lombok.Getter;
import lombok.Setter;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Message used to tell the game state about movements.
 */
@Getter
@Setter
public class MovementMessage extends ProtocolMessage {
    /** Indentifier of object */
    private String identifier = null;

    /** The direction the object is in. */
    private D3PolarCoordinate direction = null;

    /** The acceleration of the object. */
    private D3Vector acceleration = null;

    public Optional<D3PolarCoordinate> getDirection() {
        return Optional.ofNullable(direction);
    }

    public Optional<D3Vector> getAcceleration() {
        return Optional.ofNullable(acceleration);
    }

    @Override
    public List<MessageTopic> getTopics() {
        List<MessageTopic> topics = new ArrayList<>();

        topics.add(MessageTopic.MOVEMENTS);

        return topics;
    }
}
