package org.inaetics.dronessimulator.common.protocol;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class KillMessage extends ProtocolMessage {
    /** Indentifier of object */
    private String identifier = null;
    private EntityType entityType;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    @Override
    public List<MessageTopic> getTopics() {
        List<MessageTopic> res = new ArrayList<>();
        res.add(MessageTopic.STATEUPDATES);
        return res;
        // return Collections.singletonList(MessageTopic.STATEUPDATES);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KillMessage)) return false;
        KillMessage that = (KillMessage) o;
        return Objects.equals(identifier, that.identifier) &&
                entityType == that.entityType;
    }

    @Override
    public int hashCode() {

        return Objects.hash(identifier, entityType);
    }

    @Override
    public String toString() {
        return String.format("(KillMessage %s %s)", this.identifier, this.entityType);
    }
}
