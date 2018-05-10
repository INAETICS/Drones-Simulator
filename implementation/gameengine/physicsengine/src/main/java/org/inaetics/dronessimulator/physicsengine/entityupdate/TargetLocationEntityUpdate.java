package org.inaetics.dronessimulator.physicsengine.entityupdate;

import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.physicsengine.Entity;

/**
 * An update to an entity to change the position.
 */
public class TargetLocationEntityUpdate extends EntityUpdate {

    public TargetLocationEntityUpdate(D3Vector newTarget) {
        this.newTarget = newTarget;
    }

    /** The new target location of the entity. */
    private final D3Vector newTarget;

    @Override
    public void update(Entity entity) {
        if (entity instanceof Entity.DroneEntity)
            ((Entity.DroneEntity) entity).setTargetPosition(this.newTarget);
    }
}
