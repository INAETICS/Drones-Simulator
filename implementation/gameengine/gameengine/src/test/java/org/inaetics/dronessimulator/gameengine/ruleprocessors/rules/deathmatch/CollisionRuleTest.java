package org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.deathmatch;

import org.inaetics.dronessimulator.gameengine.common.gameevent.CollisionStartEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.DamageEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.DestroyBulletEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.common.state.Bullet;
import org.inaetics.dronessimulator.gameengine.common.state.Drone;
import org.inaetics.dronessimulator.pubsub.protocol.EntityType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CollisionRuleTest {
    private CollisionRule rule;
    private Drone droneEntity1;
    private Drone droneEntity2;
    private Bullet bulletEntity;

    @Before
    public void setup() {
        rule = new CollisionRule();
        droneEntity1 = mock(Drone.class);
        droneEntity2 = mock(Drone.class);
        when(droneEntity1.getType()).thenReturn(EntityType.DRONE);
        when(droneEntity2.getType()).thenReturn(EntityType.DRONE);
        bulletEntity = mock(Bullet.class);
        when(bulletEntity.getType()).thenReturn(EntityType.BULLET);
        when(bulletEntity.getEntityId()).thenReturn(1);
        when(bulletEntity.getFiredBy()).thenReturn(droneEntity2);
        when(bulletEntity.getDmg()).thenReturn(15);
    }

    @Test
    public void testProcessForTwoDrones() throws Exception {
        //Create a GameEngineEvent that is a CollisionStartEvent with two drones that collide
        CollisionStartEvent event = new CollisionStartEvent(droneEntity1, droneEntity2);

        List<GameEngineEvent> result = rule.process(event);

        Assert.assertEquals(1 + 2, result.size()); //The message itself, and two new damage messages
        Assert.assertEquals(event, result.get(0));
        Assert.assertEquals(new DamageEvent(droneEntity1, 100), result.get(1));
        Assert.assertEquals(new DamageEvent(droneEntity2, 100), result.get(2));
    }

    @Test
    public void testProcessForTwoBullets() throws Exception {
        //Create a GameEngineEvent that is a CollisionStartEvent with two bullets that collide
        CollisionStartEvent event = new CollisionStartEvent(bulletEntity, bulletEntity);

        List<GameEngineEvent> result = rule.process(event);

        Assert.assertEquals(1 + 2, result.size()); //The message itself, and two new damage messages
        Assert.assertEquals(event, result.get(0));
        Assert.assertEquals(new DestroyBulletEvent(1), result.get(1));
        Assert.assertEquals(new DestroyBulletEvent(1), result.get(2));
    }

    @Test
    public void testProcessForDronesAndBullet() throws Exception {
        //Create a GameEngineEvent that is a CollisionStartEvent with one drone and one bullet that collide
        CollisionStartEvent event1 = new CollisionStartEvent(droneEntity1, bulletEntity);
        checkDroneBulletCollisionForEvent(event1);
        CollisionStartEvent event2 = new CollisionStartEvent(bulletEntity, droneEntity1);
        checkDroneBulletCollisionForEvent(event2);
    }

    private void checkDroneBulletCollisionForEvent(CollisionStartEvent event) {
        List<GameEngineEvent> result = rule.process(event);

        Assert.assertEquals(3, result.size());
        Assert.assertEquals(event, result.get(0));
        Assert.assertEquals(new DestroyBulletEvent(1), result.get(1));
        Assert.assertEquals(new DamageEvent(droneEntity1, 15), result.get(2));
    }

    @Test
    public void testProcessForDronesAndOwnBullet() {
        CollisionStartEvent event = new CollisionStartEvent(droneEntity2, bulletEntity);
        List<GameEngineEvent> result = rule.process(event);
        Assert.assertEquals(0, result.size()); //The message itself should not be bubbled up since the collision has no impact
    }

}