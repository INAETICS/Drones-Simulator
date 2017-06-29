package org.inaetics.dronessimulator.common.protocol;

import org.inaetics.dronessimulator.common.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.D3Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Message used to tell the game state about movements.
 */
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

    public void setDirection(D3PolarCoordinate direction) {
        this.direction = direction;
    }

    public Optional<D3Vector> getAcceleration() {
        return Optional.ofNullable(acceleration);
    }

    public void setAcceleration(D3Vector acceleration) {
        this.acceleration = acceleration;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(String identifier){ this.identifier = identifier; }

    @Override
    public List<MessageTopic> getTopics() {
        List<MessageTopic> topics = new ArrayList<>();

        topics.add(MessageTopic.MOVEMENTS);

        return topics;
    }
}
