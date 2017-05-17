package org.inaetics.dronessimulator.common.protocol;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KillMessage extends ProtocolMessage {
    /** Indentifier of object */
    private String identifier = null;
    private EntityType entityType;

    public Optional<String> getIdentifier(){ return Optional.ofNullable(identifier); }

    public void setIdentifier(String identifier){ this.identifier = identifier; }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    @Override
    public List<MessageTopic> getTopics() {
        List<MessageTopic> topics = new ArrayList<>();

        topics.add(MessageTopic.STATEUPDATES);

        return topics;
    }
}
