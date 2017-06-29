package org.inaetics.dronessimulator.gameengine.physicsenginedriver;

import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Interface for a physics engine driver. The physics engine drivers interfaces the simulator with any physics engine.
 */
public interface IPhysicsEngineDriver {
    /**
     * Returns the queue for events coming from the physics engine.
     * @return The outgoing queue.
     */
    LinkedBlockingQueue<GameEngineEvent> getOutgoingQueue();

    /**
     * Adds a new game entity to the physics engine.
     * @param entity The entity to add.
     * @param protocolId The protocol id of the entity.
     */
    void addNewEntity(GameEntity entity, String protocolId);

    /**
     * Removes a game entity from the physics engine.
     * @param entityId The id of the entity to remove.
     */
    void removeEntity(int entityId);

    /**
     * Removes a game entity from the physics engine.
     * @param protocolId The protocol id of the entity to remove.
     */
    void removeEntity(String protocolId);

    /**
     * Inflict damage on the given game entity.
     * @param entityId The id of the entity.
     * @param damage The damage to inflict.
     */
    void damageEntity(int entityId, int damage);

    /**
     * Inflict damage on the given game entity.
     * @param protocolId The protocol id of the entity.
     * @param damage The damage to inflict.
     */
    void damageEntity(String protocolId, int damage);

    /**
     * Change the position of the given game entity.
     * @param entityId The id of the entity.
     * @param newPosition The new position.
     */
    void changePositionEntity(int entityId, D3Vector newPosition);

    /**
     * Change the position of the given game entity.
     * @param protocolId The protocol id of the entity.
     * @param newPosition The new position.
     */
    void changePositionEntity(String protocolId, D3Vector newPosition);

    /**
     * Change the velocity of the given game entity.
     * @param entityId The id of the entity.
     * @param newVelocity The new velocity.
     */
    void changeVelocityEntity(int entityId, D3Vector newVelocity);

    /**
     * Change the velocity of the given game entry.
     * @param protocolId The protocol id of the entity.
     * @param newVelocity The new velocity.
     */
    void changeVelocityEntity(String protocolId, D3Vector newVelocity);

    /**
     * Change the acceleration of the given game entity.
     * @param entityId The id of the entity.
     * @param newAcceleration The new acceleration.
     */
    void changeAccelerationEntity(int entityId, D3Vector newAcceleration);

    /**
     * Change the acceleration of the given game entity.
     * @param protocolId The protocol id of the entity.
     * @param newAcceleration The new acceleration.
     */
    void changeAccelerationEntity(String protocolId, D3Vector newAcceleration);

    void startEngine();
    void pauseEngine();
    void resumeEngine();
    void stopEngine();
}
