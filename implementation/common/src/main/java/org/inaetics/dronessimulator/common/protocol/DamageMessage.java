package org.inaetics.dronessimulator.common.protocol;

import java.util.Collections;
import java.util.List;

/**
 * Protocol message describing that an entity has been damaged
 */
public class DamageMessage extends ProtocolMessage {
    /**
     * The id of the entity which is damaged
     */
    private String entityId;
    /**
     * The type of the entity which is damaged
     */
    private EntityType entityType;

    /**
     * How much has the entity has been damaged
     */
    private int damage;

    @Override
    public List<MessageTopic> getTopics() {
        return Collections.singletonList(MessageTopic.STATEUPDATES);
    }

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
}
