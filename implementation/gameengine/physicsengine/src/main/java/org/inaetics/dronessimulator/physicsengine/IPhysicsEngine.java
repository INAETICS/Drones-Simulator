package org.inaetics.dronessimulator.physicsengine;

import org.inaetics.dronessimulator.physicsengine.entityupdate.EntityUpdate;

import java.util.Collection;

/**
 * Interface for a physics engine.
 */
public interface IPhysicsEngine {
    /**
     * Sets the observer for the events triggered by this engine.
     * @param engineObserver The observer for engine events.
     */
    void setObserver(PhysicsEngineEventObserver engineObserver);

    /**
     * Sets the time between broadcasts. By default, state broadcasting is off.
     * @param i The time between broadcasts in milliseconds.
     */
    void setTimeBetweenBroadcastms(long i);

    /**
     * Adds the new entity to the physics engine.
     * @threadsafe
     * @param entity The entity to add.
     */
    void addInsert(Entity entity);

    /**
     * Adds any new entities to the physics engine.
     * @threadsafe
     * @param entities The entities to add.
     */
    void addInserts(Collection<Entity> entities);

    /**
     * Change entity by applying the update
     * @threadsafe
     * @param entityId The entity to apply the update to.
     * @param entityUpdate The update to apply.
     */
    void addUpdate(Integer entityId, EntityUpdate entityUpdate);

    /**
     * Changes the given entity by applying the given updates.
     * @threafsafe
     * @param entityId The entity to apply the updates to.
     * @param entityUpdates The updates to apply.
     */
    void addUpdates(Integer entityId, Collection<EntityUpdate> entityUpdates);

    /**
     * Removes the entity identified by removal from the engine.
     * @threadsafe
     * @param entityId The entity to remove.
     */
    void addRemoval(Integer entityId);

    /**
     * Removes the entities identified by removals from the engine.
     * @threadsafe
     * @param entityIds The entities to remove.
     */
    void addRemovals(Collection<Integer> entityIds);

    void startEngine();
    void stopEngine();
    void pauseEngine();
    void resumeEngine();
}
