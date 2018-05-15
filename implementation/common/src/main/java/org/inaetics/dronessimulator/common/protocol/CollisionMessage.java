package org.inaetics.dronessimulator.common.protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

    @Override
    public String toString() {
        return String.format("(CollisionMessage %s %s, %s, %s)", this.e1Identifier, this.e1Type, this.e2Identifier, this.e2Type);
    }

    @Override
    public List<MessageTopic> getTopics() {
        List<MessageTopic> res = new ArrayList<>();
        res.add(MessageTopic.STATEUPDATES);
        return res;
//        return Collections.singletonList(MessageTopic.STATEUPDATES);
    }

    public EntityType getE1Type() {
        return e1Type;
    }

    public void setE1Type(EntityType e1Type) {
        this.e1Type = e1Type;
    }

    public String getE1Identifier() {
        return e1Identifier;
    }

    public void setE1Identifier(String e1Identifier) {
        this.e1Identifier = e1Identifier;
    }

    public EntityType getE2Type() {
        return e2Type;
    }

    public void setE2Type(EntityType e2Type) {
        this.e2Type = e2Type;
    }

    public String getE2Identifier() {
        return e2Identifier;
    }

    public void setE2Identifier(String e2Identifier) {
        this.e2Identifier = e2Identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollisionMessage that = (CollisionMessage) o;
        return e1Type == that.e1Type &&
                Objects.equals(e1Identifier, that.e1Identifier) &&
                e2Type == that.e2Type &&
                Objects.equals(e2Identifier, that.e2Identifier);
    }

    @Override
    public int hashCode() {

        return Objects.hash(e1Type, e1Identifier, e2Type, e2Identifier);
    }
}
