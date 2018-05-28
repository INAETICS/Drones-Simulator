package org.inaetics.dronessimulator.common.protocol;

import org.inaetics.dronessimulator.common.vector.D3Vector;

import java.util.*;

public class TargetMoveLocationMessage extends ProtocolMessage {
    public TargetMoveLocationMessage() {
    }

    /** Indentifier of object */
    private String identifier = null;

    /** The target location for the object. */
    private D3Vector targetLocation = null;

    public D3Vector getTargetLocation() {
        return targetLocation;
    }

    @Override
    public List<MessageTopic> getTopics() {
        List<MessageTopic> res = new ArrayList<>();
        res.add(MessageTopic.MOVEMENTS);
        return res;
        //return Collections.singletonList(MessageTopic.MOVEMENTS);
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
