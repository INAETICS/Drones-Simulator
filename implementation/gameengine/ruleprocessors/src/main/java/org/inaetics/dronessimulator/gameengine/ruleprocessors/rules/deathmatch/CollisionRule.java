package org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.deathmatch;


import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.gameengine.common.gameevent.CollisionStartEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.DestroyBulletEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.common.state.Bullet;
import org.inaetics.dronessimulator.gameengine.common.state.Drone;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.Processor;

import java.util.ArrayList;
import java.util.List;

public class CollisionRule extends Processor {
    public static final int collisionDmg = 20;

    public List<GameEngineEvent> process(GameEngineEvent msg) {
        List<GameEngineEvent> results = new ArrayList<>();

        if(msg instanceof CollisionStartEvent) {
            CollisionStartEvent collision = (CollisionStartEvent) msg;
            GameEntity e1 = collision.getE1();
            GameEntity e2 = collision.getE2();
            EntityType e1Type = e1.getType();
            EntityType e2Type = e2.getType();

            if(e1Type.equals(EntityType.DRONE) && e2Type.equals(EntityType.DRONE)) {
                // Drone vs. Drone collision. Damage both
                ((Drone) e1).damage(collisionDmg);
                ((Drone) e2).damage(collisionDmg);
            } else if(e1Type.equals(EntityType.BULLET) && e2Type.equals(EntityType.BULLET)) {
                // Bullet vs. bullet. Destroy both
                results.add(new DestroyBulletEvent(e1.getEntityId()));
                results.add(new DestroyBulletEvent(e2.getEntityId()));
            } else if(e1Type.equals(EntityType.DRONE) && e2Type.equals(EntityType.BULLET)) {
                // Drone vs. bullet. Damage drone
                Bullet bullet = (Bullet) e2;
                Drone drone = (Drone) e1;

                results.add(new DestroyBulletEvent(bullet.getEntityId()));
                drone.damage(bullet.getDmg());
            } else if(e1Type.equals(EntityType.BULLET) && e2Type.equals(EntityType.DRONE)) {
                // Drone vs. bullet. Damage drone
                Bullet bullet = (Bullet) e1;
                Drone drone = (Drone) e2;

                results.add(new DestroyBulletEvent(bullet.getEntityId()));
                drone.damage(bullet.getDmg());

            } else {
                Logger.getLogger(CollisionRule.class).error("Found a collision with unknown rules. Collision between: {} & {} ", e1Type, e2Type);
            }
        }

        return results;
    }

}