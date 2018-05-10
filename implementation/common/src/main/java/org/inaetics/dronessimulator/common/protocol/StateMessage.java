package org.inaetics.dronessimulator.common.protocol;

import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Message used by the game state to communicate state changes to other nodes.
 */

public class StateMessage extends ProtocolMessage {
    private final LocalTime timestamp;

    public StateMessage(LocalTime timestamp) { //For testing purposes.
        this.timestamp = timestamp;
    }
    /** Identifier of object that this state message is about. */
    private String identifier = null;

    private EntityType type;

    private D3Vector position = null;

    private D3PolarCoordinate direction = null;

    private D3Vector velocity = null;

    private D3Vector acceleration = null;

    private Integer hp = null;

    //For testing purposes, you should be able to set the timestamp. In real-world use, you just want it to work out
    // of the box. This constructor is used for real world use.
    public StateMessage() {
        timestamp = LocalTime.now();
    }



    public Optional<D3Vector> getPosition() {
        return Optional.ofNullable(position);
    }

    public Optional<D3PolarCoordinate> getDirection() {
        return Optional.ofNullable(direction);
    }

    public Optional<D3Vector> getVelocity() {
        return Optional.ofNullable(velocity);
    }

    public Optional<D3Vector> getAcceleration() {
        return Optional.ofNullable(acceleration);
    }

    public Optional<Integer> getHp() {
        return Optional.ofNullable(hp);
    }

    @Override
    public List<MessageTopic> getTopics() {
        return Collections.singletonList(MessageTopic.STATEUPDATES);
    }

    @Override
    public String toString() {
        return String.format("(StateMessage %s %s, %s, %s, %s, %s)", this.identifier, this.position, this.direction, this.velocity, this.acceleration, this.hp);
    }

    public LocalTime getTimestamp() {
        return timestamp;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }

    public void setPosition(D3Vector position) {
        this.position = position;
    }

    public void setDirection(D3PolarCoordinate direction) {
        this.direction = direction;
    }

    public void setVelocity(D3Vector velocity) {
        this.velocity = velocity;
    }

    public void setAcceleration(D3Vector acceleration) {
        this.acceleration = acceleration;
    }

    public void setHp(Integer hp) {
        this.hp = hp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StateMessage)) return false;
        StateMessage that = (StateMessage) o;
        return Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(identifier, that.identifier) &&
                type == that.type &&
                Objects.equals(position, that.position) &&
                Objects.equals(direction, that.direction) &&
                Objects.equals(velocity, that.velocity) &&
                Objects.equals(acceleration, that.acceleration) &&
                Objects.equals(hp, that.hp);
    }

    @Override
    public int hashCode() {

        return Objects.hash(timestamp, identifier, type, position, direction, velocity, acceleration, hp);
    }
}
