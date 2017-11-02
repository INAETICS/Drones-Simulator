package org.inaetics.dronessimulator.common.protocol;


import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * A message describing a collision between 2 entities
 */
@Getter
@Setter
public class CollisionMessage extends ProtocolMessage {
    /**
     * The type of the first entity
     */
    private EntityType e1Type;
    /**
     * The id of the first entity
     */
    private String e1Identifier;

    /**
     * The type of the second entity
     */
    private EntityType e2Type;
    /**
     * The id of the second entity
     */
    private String e2Identifier;

    @Override
    public String toString() {
        return String.format("(CollisionMessage %s %s, %s, %s)", this.e1Identifier, this.e1Type, this.e2Identifier, this.e2Type);
    }

    @Override
    public List<MessageTopic> getTopics() {
        return Collections.singletonList(MessageTopic.STATEUPDATES);
    }
}
