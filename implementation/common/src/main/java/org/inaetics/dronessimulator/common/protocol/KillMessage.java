package org.inaetics.dronessimulator.common.protocol;


import java.util.Collections;
import java.util.List;

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
        return Collections.singletonList(MessageTopic.STATEUPDATES);
    }

    @Override
    public String toString() {
        return String.format("(KillMessage %s %s)", this.identifier, this.entityType);
    }
}
