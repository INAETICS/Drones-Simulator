package org.inaetics.dronessimulator.common;

import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.protocol.CollisionMessage;
import org.inaetics.dronessimulator.common.protocol.CompressedProtocolMessage;
import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.common.protocol.DamageMessage;
import org.inaetics.dronessimulator.common.protocol.FireBulletMessage;
import org.inaetics.dronessimulator.common.protocol.GameFinishedMessage;
import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.common.protocol.MovementMessage;
import org.inaetics.dronessimulator.common.protocol.RequestArchitectureStateChangeMessage;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.common.protocol.TargetMoveLocationMessage;
import org.inaetics.dronessimulator.common.protocol.TextMessage;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.pubsub.impl.serialization.jackson.JacksonSerializer;


import java.util.List;
import java.util.ArrayList;

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

    @Test
    public void testCompressedProtocolMessage() {
        CompressedProtocolMessage emptyCpm = new CompressedProtocolMessage();
        List<StateMessage> msgs = new ArrayList<>();
        msgs.add(new StateMessage());
        CompressedProtocolMessage filledCpm = new CompressedProtocolMessage(msgs);
        byte[] serializedEmptyCpm = jacksonSerializer.serialize(emptyCpm);
        byte[] serializedFilledCpm = jacksonSerializer.serialize(filledCpm);
        System.out.println(new String(serializedFilledCpm));
        CompressedProtocolMessage deserializedEmptyCpm =
                (CompressedProtocolMessage)jacksonSerializer.deserialize(CompressedProtocolMessage.class.getName(),serializedEmptyCpm);
        CompressedProtocolMessage deserializedFilledCpm =
                (CompressedProtocolMessage) jacksonSerializer.deserialize(CompressedProtocolMessage.class.getName(),serializedFilledCpm);
        Assert.assertEquals(emptyCpm,deserializedEmptyCpm);
        Assert.assertEquals(filledCpm,deserializedFilledCpm);
    }

    @Test
    public void testDamageMessage () {
        DamageMessage emptyDm = new DamageMessage();
        DamageMessage filledDm = new DamageMessage();
        filledDm.setDamage(10);
        filledDm.setEntityId("entityId");
        filledDm.setEntityType(EntityType.DRONE);
        byte[] serializedEmptyDm = jacksonSerializer.serialize(emptyDm);
        byte[] serializedFilledDm = jacksonSerializer.serialize(filledDm);
        DamageMessage deserializedEmptyDm =
                (DamageMessage) jacksonSerializer.deserialize(DamageMessage.class.getName(),serializedEmptyDm);
        DamageMessage deserializedFilledDm =
                (DamageMessage) jacksonSerializer.deserialize(DamageMessage.class.getName(),serializedFilledDm);
        Assert.assertEquals(emptyDm,deserializedEmptyDm);
        Assert.assertEquals(filledDm,deserializedFilledDm);

    }

    @Test
    public void testFireBulletMessage () {
        FireBulletMessage emptyFbm = new FireBulletMessage();
        FireBulletMessage filledFbm = new FireBulletMessage();
        filledFbm.setDamage(10);
        filledFbm.setFiredById("fireById");
        byte[] serializedEmptyFbm = jacksonSerializer.serialize(emptyFbm);
        byte[] serializedFilledFbm = jacksonSerializer.serialize(filledFbm);
        FireBulletMessage deserializedEmptyFbm =
                (FireBulletMessage) jacksonSerializer.deserialize(FireBulletMessage.class.getName(),serializedEmptyFbm);
        FireBulletMessage deserializedFilledFbm =
                (FireBulletMessage) jacksonSerializer.deserialize(FireBulletMessage.class.getName(),serializedFilledFbm);
        Assert.assertEquals(emptyFbm,deserializedEmptyFbm);
        Assert.assertEquals(filledFbm,deserializedFilledFbm);
    }

    @Test
    public void testGameFinishedMessage () {
        GameFinishedMessage emptyGfm = new GameFinishedMessage();
        String winner = "winner";
        GameFinishedMessage filledGfm = new GameFinishedMessage(winner);
        byte[] serializedEmptyGfm = jacksonSerializer.serialize(emptyGfm);
        byte[] serializedFilledGfm = jacksonSerializer.serialize(filledGfm);
        GameFinishedMessage deserializedEmptyFbm =
                (GameFinishedMessage) jacksonSerializer.deserialize(GameFinishedMessage.class.getName(),serializedEmptyGfm);
        GameFinishedMessage deserializedFilledGfm =
                (GameFinishedMessage) jacksonSerializer.deserialize(GameFinishedMessage.class.getName(),serializedFilledGfm);
        Assert.assertEquals(emptyGfm,deserializedEmptyFbm);
        Assert.assertEquals(filledGfm,deserializedFilledGfm);
    }

    @Test
    public void testKillMessage () {
        KillMessage emptyKm =  new KillMessage();
        KillMessage filledKm = new KillMessage();
        filledKm.setEntityType(EntityType.DRONE);
        filledKm.setIdentifier("identifier");
        byte[] serializedEmptyKm = jacksonSerializer.serialize(emptyKm);
        byte[] serializedFilledKm = jacksonSerializer.serialize(filledKm);
        KillMessage deserializedEmptyKm =
                (KillMessage) jacksonSerializer.deserialize(KillMessage.class.getName(),serializedEmptyKm);
        KillMessage deserializedFilledKm =
                (KillMessage) jacksonSerializer.deserialize(KillMessage.class.getName(),serializedFilledKm);
        Assert.assertEquals(emptyKm,deserializedEmptyKm);
        Assert.assertEquals(filledKm,deserializedFilledKm);
    }

    @Test
    public void testMovementMessage () {
        MovementMessage emptyMm =  new MovementMessage();
        MovementMessage filledMm = new MovementMessage();
        filledMm.setAcceleration (new D3Vector(1,1,1));
        filledMm.setDirection(new D3PolarCoordinate(10,10,10));
        filledMm.setVelocity(new D3Vector(2,2,2));
        filledMm.setIdentifier("identifier");
        byte[] serializedEmptyMm = jacksonSerializer.serialize(emptyMm);
        byte[] serializedFilledMm = jacksonSerializer.serialize(filledMm);
        MovementMessage deserializedEmptyMm =
                (MovementMessage) jacksonSerializer.deserialize(MovementMessage.class.getName(),serializedEmptyMm);
        MovementMessage deserializedFilledMm =
                (MovementMessage) jacksonSerializer.deserialize(MovementMessage.class.getName(),serializedFilledMm);
        Assert.assertEquals(emptyMm,deserializedEmptyMm);
        Assert.assertEquals(filledMm,deserializedFilledMm);
    }

    @Test
    public void testRequestArchitectureStateChangeMessage (){
        RequestArchitectureStateChangeMessage emptyRm = new RequestArchitectureStateChangeMessage();
        RequestArchitectureStateChangeMessage filledRm = new RequestArchitectureStateChangeMessage(SimulationAction.INIT);
        byte[] serializedEmptyRm = jacksonSerializer.serialize(emptyRm);
        byte[] serializedFilledRm = jacksonSerializer.serialize(filledRm);
        RequestArchitectureStateChangeMessage deserializedEmptyRm =
                (RequestArchitectureStateChangeMessage) jacksonSerializer.deserialize(RequestArchitectureStateChangeMessage.class.getName(),serializedEmptyRm);
        RequestArchitectureStateChangeMessage deserializedFilledRm =
                (RequestArchitectureStateChangeMessage) jacksonSerializer.deserialize(RequestArchitectureStateChangeMessage.class.getName(),serializedFilledRm);
        Assert.assertEquals(emptyRm,deserializedEmptyRm);
        Assert.assertEquals(filledRm,deserializedFilledRm);

    }

    @Test
    public void testStateMessage () {
        StateMessage emptySm = new StateMessage();
        StateMessage filledSm = new StateMessage();
        filledSm.setAcceleration(new D3Vector(1,1,1));
        filledSm.setDirection(new D3PolarCoordinate(10,10,5));
        filledSm.setHp(200);
        filledSm.setIdentifier("identifier");
        filledSm.setPosition(new D3Vector(1,1,1));
        filledSm.setType(EntityType.DRONE);
        filledSm.setVelocity(new D3Vector(1,1,1));
        byte[] serializedEmptySm = jacksonSerializer.serialize(emptySm);
        byte[] serializedFilledSm = jacksonSerializer.serialize(filledSm);

        StateMessage deserializedFilledSm =
                (StateMessage) jacksonSerializer.deserialize(StateMessage.class.getName(),serializedFilledSm);
        StateMessage deserializedEmptySm =
                (StateMessage) jacksonSerializer.deserialize(StateMessage.class.getName(),serializedEmptySm);
        Assert.assertEquals(emptySm,deserializedEmptySm);
        Assert.assertEquals(filledSm,deserializedFilledSm);
    }

    @Test
    public void testTargetMoveLocationMessage () {
        TargetMoveLocationMessage emptyTmlm = new TargetMoveLocationMessage();
        TargetMoveLocationMessage filledTmlm = new TargetMoveLocationMessage();
        filledTmlm.setIdentifier("identifier");
        filledTmlm.setTargetLocation(new D3Vector(1,1,1));
        byte[] serializedEmptyTmlm = jacksonSerializer.serialize(emptyTmlm);
        byte[] serializedFilledTmlm = jacksonSerializer.serialize(filledTmlm);
        TargetMoveLocationMessage deserializedEmptyTmlm =
                (TargetMoveLocationMessage) jacksonSerializer.deserialize(TargetMoveLocationMessage.class.getName(),serializedEmptyTmlm);
        TargetMoveLocationMessage deserializedFilledTmlm =
                (TargetMoveLocationMessage) jacksonSerializer.deserialize(TargetMoveLocationMessage.class.getName(),serializedFilledTmlm);
        Assert.assertEquals(emptyTmlm,deserializedEmptyTmlm);
        Assert.assertEquals(filledTmlm,deserializedFilledTmlm);
    }
    @Test
    public void testTextMessage () {
        TextMessage emptyTem = new TextMessage();
        TextMessage filledTem = new TextMessage();
        filledTem.setText("text");
        byte[] serializedEmptyTem = jacksonSerializer.serialize(emptyTem);
        byte[] serializedFilledTem = jacksonSerializer.serialize(filledTem);
        TextMessage deserializedEmptyTem =
                (TextMessage) jacksonSerializer.deserialize(TextMessage.class.getName(),serializedEmptyTem);
        TextMessage deserializedFilledTem =
                (TextMessage) jacksonSerializer.deserialize(TextMessage.class.getName(),serializedFilledTem);
        Assert.assertEquals(emptyTem,deserializedEmptyTem);
        Assert.assertEquals(filledTem,deserializedFilledTem);
    }

}
