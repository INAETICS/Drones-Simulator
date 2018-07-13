package org.inaetics.dronessimulator.physicsengine.entityupdate;

import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.physicsengine.Entity;

/**
 * An update to an entity to change the acceleration.
 */
public class AccelerationEntityUpdate extends EntityUpdate {

    public AccelerationEntityUpdate(D3Vector newAcceleration) {
        this.newAcceleration = newAcceleration;
    }

    /** The new acceleration. */
    private final D3Vector newAcceleration;

    @Override
    public void update(Entity entity) {
        entity.setAcceleration(this.newAcceleration);
    }
}
