package org.inaetics.dronessimulator.physicsengine.entityupdate;

import lombok.AllArgsConstructor;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.physicsengine.Entity;

/**
 * An update to an entity to change the position.
 */
@AllArgsConstructor
public class PositionEntityUpdate extends EntityUpdate {
    /** The new position of the entity. */
    private final D3Vector newPosition;

    @Override
    public void update(Entity entity) {
        entity.setPosition(this.newPosition);
    }
}
