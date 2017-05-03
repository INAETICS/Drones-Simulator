package org.inaetics.dronessimulator.physicsengine.entityupdate;

import lombok.AllArgsConstructor;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.physicsengine.Entity;


/**
 * An update to an entity to change the velocity
 */
@AllArgsConstructor
public class VelocityEntityUpdate extends EntityUpdate {
    /**
     * The new velocity of the entity
     */
    private final D3Vector newVelocity;

    /**
     * How to apply the update to the entity. Set the newVelocity on entity
     * @param entity Which entity to update.
     */
    @Override
    public void update(Entity entity) {
        entity.setVelocity(this.newVelocity);
    }
}
