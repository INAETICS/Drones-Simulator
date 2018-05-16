package org.inaetics.dronessimulator.common;

import org.inaetics.dronessimulator.common.protocol.CollisionMessage;
import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.pubsub.impl.serialization.jackson.JacksonSerializer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ProtocolSerializationTest {

    private static JacksonSerializer jacksonSerializer;

    @BeforeClass
    public static void init() {
        jacksonSerializer = new JacksonSerializer();
    }

    @Test
    public void testCollisionMessage() {
        CollisionMessage emptyCm = new CollisionMessage();
        CollisionMessage filledCm = new CollisionMessage();
        filledCm.setE1Identifier("e1Identifier");
        filledCm.setE1Type(EntityType.BULLET);
        filledCm.setE2Identifier("e2Identifier");
        filledCm.setE2Type(EntityType.DRONE);
        byte[] serializedFilledCm = jacksonSerializer.serialize(filledCm);
        byte[] serializedEmptyCm = jacksonSerializer.serialize(emptyCm);
        CollisionMessage deserializedFilledCm =
                (CollisionMessage) jacksonSerializer.deserialize(CollisionMessage.class.getName(), serializedFilledCm);
        CollisionMessage deserializedEmptyCm =
                (CollisionMessage) jacksonSerializer.deserialize(CollisionMessage.class.getName(), serializedEmptyCm);
        Assert.assertEquals(filledCm, deserializedFilledCm);
        Assert.assertEquals(emptyCm, deserializedEmptyCm);
    }


}
