package org.inaetics.dronessimulator.common.protocol;

import java.util.Collections;
import java.util.List;

public class DamageMessage extends ProtocolMessage {
    private String entityId;
    private EntityType entityType;

    private int damage;

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    @Override
    public List<MessageTopic> getTopics() {
        return Collections.singletonList(MessageTopic.STATEUPDATES);
    }
}
