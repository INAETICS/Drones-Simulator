package org.inaetics.dronessimulator.physicsenginewrapper.ruleprocessors.rules;


import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.physicsenginewrapper.physicsenginemessage.CollisionStartMessage;
import org.inaetics.dronessimulator.physicsenginewrapper.physicsenginemessage.PhysicsEngineMessage;
import org.inaetics.dronessimulator.physicsenginewrapper.ruleprocessors.message.DestroyBullet;
import org.inaetics.dronessimulator.physicsenginewrapper.ruleprocessors.message.RuleMessage;
import org.inaetics.dronessimulator.physicsenginewrapper.state.Bullet;
import org.inaetics.dronessimulator.physicsenginewrapper.state.Drone;
import org.inaetics.dronessimulator.physicsenginewrapper.state.PhysicsEngineEntity;
import org.inaetics.dronessimulator.physicsenginewrapper.state.PhysicsEngineStateManager;

import java.util.List;

public class CollisionRules extends Processor {
    public static final int collisionDmg = 20;

    public void process(PhysicsEngineStateManager stateManager, PhysicsEngineMessage msg, List<RuleMessage> results) {
        if(msg instanceof CollisionStartMessage) {
            CollisionStartMessage collision = (CollisionStartMessage) msg;
            PhysicsEngineEntity e1 = stateManager.getById(collision.getE1().getId());
            PhysicsEngineEntity e2 = stateManager.getById(collision.getE2().getId());
            EntityType e1Type = e1.getType();
            EntityType e2Type = e2.getType();

            if(e1Type.equals(EntityType.DRONE) && e2Type.equals(EntityType.DRONE)) {
                // Drone vs. Drone collision. Damage both
                ((Drone) e1).damage(collisionDmg);
                ((Drone) e2).damage(collisionDmg);
            } else if(e1Type.equals(EntityType.BULLET) && e2Type.equals(EntityType.BULLET)) {
                // Bullet vs. bullet. Destroy both
                results.add(new DestroyBullet(e1.getEntityId()));
                results.add(new DestroyBullet(e2.getEntityId()));
            } else if(e1Type.equals(EntityType.DRONE) && e2Type.equals(EntityType.BULLET)) {
                // Drone vs. bullet. Damage drone
                Bullet bullet = (Bullet) e2;
                Drone drone = (Drone) e1;

                results.add(new DestroyBullet(bullet.getEntityId()));
                drone.damage(bullet.getDmg());
            } else if(e1Type.equals(EntityType.BULLET) && e2Type.equals(EntityType.DRONE)) {
                // Drone vs. bullet. Damage drone
                Bullet bullet = (Bullet) e1;
                Drone drone = (Drone) e2;

                results.add(new DestroyBullet(bullet.getEntityId()));
                drone.damage(bullet.getDmg());

            } else {
                Logger.getLogger(CollisionRules.class).error("Found a collision with unknown rules. Collision between: {} & {} ", e1Type, e2Type);
            }
        }
    }

}
