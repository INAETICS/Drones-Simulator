package org.inaetics.dronessimulator.common.protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
        List<MessageTopic> res = new ArrayList<>();
        res.add(MessageTopic.STATEUPDATES);
        return res;
        //return Collections.singletonList(MessageTopic.STATEUPDATES);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DamageMessage)) return false;
        DamageMessage that = (DamageMessage) o;
        return damage == that.damage &&
                Objects.equals(entityId, that.entityId) &&
                entityType == that.entityType;
    }

    @Override
    public int hashCode() {

        return Objects.hash(entityId, entityType, damage);
    }
}
