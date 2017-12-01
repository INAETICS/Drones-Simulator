package org.inaetics.dronessimulator.common.protocol;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Message used by the game state to communicate state changes to other nodes.
 */
@RequiredArgsConstructor //For testing purposes.
@Data
public class StateMessage extends ProtocolMessage {
    private final LocalTime timestamp;
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

}
