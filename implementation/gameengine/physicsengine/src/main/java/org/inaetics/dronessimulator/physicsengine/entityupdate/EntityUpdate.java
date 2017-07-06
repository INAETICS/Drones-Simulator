package org.inaetics.dronessimulator.physicsengine.entityupdate;

import org.inaetics.dronessimulator.physicsengine.Entity;

/**
 * Parent class of all entity update classes.
 */
public abstract class EntityUpdate {
    /**
     * How to update an entity. Applies the update directly to entity.
     * @param entity The entity to update.
     */
    public abstract void update(Entity entity);
}
