package org.inaetics.dronessimulator.physicsengine.entityupdate;


import lombok.AllArgsConstructor;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.physicsengine.Entity;

/**
 * Updates the direction of the entity
 */
@AllArgsConstructor
public class DirectionEntityUpdate extends EntityUpdate {
    /** The new direction of the entity */
    private final D3PolarCoordinate newDirection;

    @Override
    public void update(Entity entity) {
        entity.setDirection(newDirection);
    }
}
