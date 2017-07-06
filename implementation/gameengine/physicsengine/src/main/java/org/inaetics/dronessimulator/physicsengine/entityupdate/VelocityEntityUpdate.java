package org.inaetics.dronessimulator.physicsengine.entityupdate;

import lombok.AllArgsConstructor;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.physicsengine.Entity;


/**
 * An update to an entity to change the velocity.
 */
@AllArgsConstructor
public class VelocityEntityUpdate extends EntityUpdate {
    /** The new velocity of the entity. */
    private final D3Vector newVelocity;

    @Override
    public void update(Entity entity) {
        entity.setVelocity(this.newVelocity);
    }
}
