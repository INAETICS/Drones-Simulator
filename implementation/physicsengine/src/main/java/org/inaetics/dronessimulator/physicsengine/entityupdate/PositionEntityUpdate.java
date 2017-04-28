package org.inaetics.dronessimulator.physicsengine.entityupdate;


import lombok.AllArgsConstructor;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.physicsengine.Entity;


@AllArgsConstructor
/**
 * An update to an entity to change the position
 */
public class PositionEntityUpdate extends EntityUpdate {
    /**
     * The new position of the entity
     */
    private final D3Vector newPosition;

    /**
     * How to apply the update to the entity. Sets the newPosition on entity.
     * @param entity Which entity to update.
     */
    @Override
    public void update(Entity entity) {
        entity.setPosition(this.newPosition);
    }
}
