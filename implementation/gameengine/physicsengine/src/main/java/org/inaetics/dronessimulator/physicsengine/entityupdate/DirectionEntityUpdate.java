package org.inaetics.dronessimulator.physicsengine.entityupdate;

import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.physicsengine.Entity;

/**
 * Updates the direction of the entity
 */

public class DirectionEntityUpdate extends EntityUpdate {

    public DirectionEntityUpdate(D3PolarCoordinate newDirection) {
        this.newDirection = newDirection;
    }

    /** The new direction of the entity */
    private final D3PolarCoordinate newDirection;

    @Override
    public void update(Entity entity) {
        entity.setDirection(newDirection);
    }
}
