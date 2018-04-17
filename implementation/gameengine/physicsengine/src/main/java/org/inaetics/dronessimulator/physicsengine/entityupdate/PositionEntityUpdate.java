package org.inaetics.dronessimulator.physicsengine.entityupdate;

import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.physicsengine.Entity;

/**
 * An update to an entity to change the position.
 */
public class PositionEntityUpdate extends EntityUpdate {

    public PositionEntityUpdate(D3Vector newPosition) {
        this.newPosition = newPosition;
    }

    /** The new position of the entity. */
    private final D3Vector newPosition;

    @Override
    public void update(Entity entity) {
        entity.setPosition(this.newPosition);
    }
}
