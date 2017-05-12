package org.inaetics.dronessimulator.common.protocol;

import org.inaetics.dronessimulator.common.D3PoolCoordinate;
import org.inaetics.dronessimulator.common.D3Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Message used by the game state to communicate state changes to other nodes.
 */
public class StateMessage extends ProtocolMessage {
    /** Indentifier of object */
    private String identifier = null;

    /** The position of the object. */
    private D3Vector position = null;

    /** The direction the object is in. */
    private D3PoolCoordinate direction = null;

    /** The velocity of the object. */
    private D3Vector velocity = null;

    /** The acceleration of the object. */
    private D3Vector acceleration = null;

    public Optional<D3Vector> getPosition() {
        return Optional.ofNullable(position);
    }

    public void setPosition(D3Vector position) {
        this.position = position;
    }

    public Optional<D3PoolCoordinate> getDirection() {
        return Optional.ofNullable(direction);
    }

    public void setDirection(D3PoolCoordinate direction) {
        this.direction = direction;
    }

    public Optional<D3Vector> getVelocity() {
        return Optional.ofNullable(velocity);
    }

    public void setVelocity(D3Vector velocity) {
        this.velocity = velocity;
    }

    public Optional<D3Vector> getAcceleration() {
        return Optional.ofNullable(acceleration);
    }

    public void setAcceleration(D3Vector acceleration) {
        this.acceleration = acceleration;
    }

    public Optional<String> getIdentifier(){ return Optional.ofNullable(identifier); }

    public void setIdentifier(String identifier){ this.identifier = identifier; }

    @Override
    public List<MessageTopic> getTopics() {
        List<MessageTopic> topics = new ArrayList<>();

        topics.add(MessageTopic.STATEUPDATES);

        return topics;
    }

    @Override
    public String toString() {
        return String.format("(StateMessage %s, %s, %s, %s)", this.position, this.direction, this.velocity, this.acceleration);
    }
}
