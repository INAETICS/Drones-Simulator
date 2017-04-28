package org.inaetics.dronessimulator.common.protocol;


import java.util.ArrayList;
import java.util.List;

public class CollisionMessage extends ProtocolMessage {
    private EntityType e1Type;
    private int e1Id;
    private EntityType e2Type;
    private int e2Id;

    public EntityType getE1Type() {
        return e1Type;
    }

    public void setE1Type(EntityType e1Type) {
        this.e1Type = e1Type;
    }

    public int getE1Id() {
        return e1Id;
    }

    public void setE1Id(int e1Id) {
        this.e1Id = e1Id;
    }

    public EntityType getE2Type() {
        return e2Type;
    }

    public void setE2Type(EntityType e2Type) {
        this.e2Type = e2Type;
    }

    public int getE2Id() {
        return e2Id;
    }

    public void setE2Id(int e2Id) {
        this.e2Id = e2Id;
    }

    @Override
    public List<MessageTopic> getTopics() {
        List<MessageTopic> topics = new ArrayList<>();

        topics.add(MessageTopic.STATEUPDATES);

        return topics;
    }
}
