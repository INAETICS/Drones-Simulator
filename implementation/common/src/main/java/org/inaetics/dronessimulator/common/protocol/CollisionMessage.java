package org.inaetics.dronessimulator.common.protocol;


import java.util.ArrayList;
import java.util.List;

/**
 * A message describing a collision between 2 entities
 */
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

    public EntityType getE1Type() {
        return e1Type;
    }

    public void setE1Type(EntityType e1Type) {
        this.e1Type = e1Type;
    }

    public String getE1Id() {
        return e1Identifier;
    }

    public void setE1Id(String e1Id) {
        this.e1Identifier = e1Id;
    }

    public EntityType getE2Type() {
        return e2Type;
    }

    public void setE2Type(EntityType e2Type) {
        this.e2Type = e2Type;
    }

    public String getE2Id() {
        return e2Identifier;
    }

    public void setE2Id(String e2Id) {
        this.e2Identifier = e2Id;
    }

    @Override
    public String toString() {
        return String.format("(CollisionMessage %s %s, %s, %s)", this.e1Identifier, this.e1Type, this.e2Identifier, this.e2Type);
    }

    @Override
    public List<MessageTopic> getTopics() {
        List<MessageTopic> topics = new ArrayList<>();

        topics.add(MessageTopic.STATEUPDATES);

        return topics;
    }
}
