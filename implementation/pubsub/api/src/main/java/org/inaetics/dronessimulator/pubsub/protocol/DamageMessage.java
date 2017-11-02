package org.inaetics.dronessimulator.pubsub.protocol;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * Protocol message describing that an entity has been damaged
 */
@Getter
@Setter
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
}
