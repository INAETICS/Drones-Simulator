package org.inaetics.dronessimulator.physicsengine.entityupdate;

import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.physicsengine.Entity;


/**
 * An update to an entity to change the velocity.
 */
public class VelocityEntityUpdate extends EntityUpdate {

    public VelocityEntityUpdate(D3Vector newVelocity) {
        this.newVelocity = newVelocity;
    }

    /** The new velocity of the entity. */
    private final D3Vector newVelocity;

    @Override
    public void update(Entity entity) {
        entity.setVelocity(this.newVelocity);
    }
}
