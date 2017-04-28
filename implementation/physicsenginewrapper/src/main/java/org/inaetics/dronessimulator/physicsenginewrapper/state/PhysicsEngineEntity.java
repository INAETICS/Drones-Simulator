package org.inaetics.dronessimulator.physicsenginewrapper.state;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.physicsengine.Entity;

@Getter
@AllArgsConstructor
public abstract class PhysicsEngineEntity {
    private final int entityId;
    private D3Vector position;
    private D3Vector velocity;
    private D3Vector acceleration;

    public void updateFromEngine(Entity entity) {
        if(entityId == entity.getId()) {
            this.position = entity.getPosition();
            this.velocity = entity.getVelocity();
            this.acceleration = entity.getAcceleration();
        } else {
            Logger.getLogger(PhysicsEngineEntity.class).fatal("Tried to update state from entity, but ids did not match. Received: " + entity.getId() + ". Needed: " + this.entityId);
        }
    }

    public abstract EntityType getType();
}
