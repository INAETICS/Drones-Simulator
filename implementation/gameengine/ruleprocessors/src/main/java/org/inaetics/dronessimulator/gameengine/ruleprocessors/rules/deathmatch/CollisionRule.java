package org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.deathmatch;


import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.gameengine.common.gameevent.CollisionStartEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.DamageEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.DestroyBulletEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.common.state.Bullet;
import org.inaetics.dronessimulator.gameengine.common.state.Drone;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.Rule;
import org.inaetics.dronessimulator.pubsub.protocol.EntityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rule to determine what happens on a collision
 * Bullets destroy each other
 * Bullet with drone damages the drone and kills the bullet
 * Drones colliding damages both drones
 */
public class CollisionRule extends Rule {
    /** How much damage a collision between drones causes */
    private static final int COLLISION_DAMAGE = Drone.DRONE_MAX_HEALTH;

    @Override
    public void configRule() {
        // Nothing to config
    }

    @Override
    public List<GameEngineEvent> process(GameEngineEvent msg) {
        List<GameEngineEvent> results;

        if(msg instanceof CollisionStartEvent) {
            CollisionStartEvent collision = (CollisionStartEvent) msg;
            GameEntity e1 = collision.getE1();
            GameEntity e2 = collision.getE2();

            results = handleCollision(e1, e2);

            // Only notify architecture of collision if the collision has consequences
            if(!results.isEmpty()) {
                results.add(0, msg);
            }
        } else {
            results = Collections.singletonList(msg);
        }

        return results;
    }

    /**
     * How to handle a collision
     * @param e1 Entity colliding
     * @param e2 Entity colliding
     * @return The events due to the collision
     */
    private List<GameEngineEvent> handleCollision(GameEntity e1, GameEntity e2) {
        //TODO refactor and make this more efficient for more possible entities.
        List<GameEngineEvent> results = new ArrayList<>();

        EntityType e1Type = e1.getType();
        EntityType e2Type = e2.getType();

        if(e1Type.equals(EntityType.DRONE) && e2Type.equals(EntityType.DRONE)) {
            // Drone vs. Drone collision. Damage both
            results.add(new DamageEvent(e1, COLLISION_DAMAGE));
            results.add(new DamageEvent(e2, COLLISION_DAMAGE));
        } else if(e1Type.equals(EntityType.BULLET) && e2Type.equals(EntityType.BULLET)) {
            // Bullet vs. bullet. Destroy both
            results.add(new DestroyBulletEvent(e1.getEntityId()));
            results.add(new DestroyBulletEvent(e2.getEntityId()));
        } else if(e1Type.equals(EntityType.DRONE) && e2Type.equals(EntityType.BULLET)) {
            // Drone vs. bullet. Damage drone
            Bullet bullet = (Bullet) e2;
            Drone drone = (Drone) e1;

            if(!bullet.getFiredBy().equals(drone)) {
                results.add(new DestroyBulletEvent(bullet.getEntityId()));
                results.add(new DamageEvent(drone, bullet.getDmg()));
            }
        } else if(e1Type.equals(EntityType.BULLET) && e2Type.equals(EntityType.DRONE)) {
            // Drone vs. bullet. Damage drone
            Bullet bullet = (Bullet) e1;
            Drone drone = (Drone) e2;

            if(!bullet.getFiredBy().equals(drone)) {
                results.add(new DestroyBulletEvent(bullet.getEntityId()));
                results.add(new DamageEvent(drone, bullet.getDmg()));
            }
        } else {
            Logger.getLogger(CollisionRule.class).error("Found a collision with unknown rules. Collision between: {} & {} ", e1Type, e2Type);
        }

        return results;
    }

}
