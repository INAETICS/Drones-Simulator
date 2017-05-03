package org.inaetics.dronessimulator.physicsengine.entityupdate;


import lombok.AllArgsConstructor;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.physicsengine.Entity;

@AllArgsConstructor
/**
 * An update to an entity to change the acceleration
 */
public class AccelerationEntityUpdate extends EntityUpdate {
    /**
     * The new acceleration
     */
    private final D3Vector newAcceleration;

    /**
     * How to update the entity. Sets the newAcceleration on entity.
     * @param entity Which entity to apply the update on
     */
    @Override
    public void update(Entity entity) {
        entity.setAcceleration(this.newAcceleration);
    }
}
