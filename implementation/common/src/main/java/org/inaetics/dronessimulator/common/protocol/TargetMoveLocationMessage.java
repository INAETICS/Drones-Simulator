package org.inaetics.dronessimulator.common.protocol;

import org.inaetics.dronessimulator.common.vector.D3Vector;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TargetMoveLocationMessage extends ProtocolMessage {
    public TargetMoveLocationMessage() {
    }

    /** Indentifier of object */
    private String identifier = null;

    /** The target location for the object. */
    private D3Vector targetLocation = null;

    public Optional<D3Vector> getTargetLocation() {
        return Optional.ofNullable(targetLocation);
    }

    @Override
    public List<MessageTopic> getTopics() {
        return Collections.singletonList(MessageTopic.MOVEMENTS);
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setTargetLocation(D3Vector targetLocation) {
        this.targetLocation = targetLocation;
    }

    @Override
    public String toString() {
        return "TargetMoveLocationMessage{" +
                "identifier='" + identifier + '\'' +
                ", targetLocation=" + targetLocation +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TargetMoveLocationMessage)) return false;
        TargetMoveLocationMessage that = (TargetMoveLocationMessage) o;
        return Objects.equals(identifier, that.identifier) &&
                Objects.equals(targetLocation, that.targetLocation);
    }

    @Override
    public int hashCode() {

        return Objects.hash(identifier, targetLocation);
    }
}
