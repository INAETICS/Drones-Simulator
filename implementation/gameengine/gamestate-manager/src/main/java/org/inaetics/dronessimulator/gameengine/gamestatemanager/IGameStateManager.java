package org.inaetics.dronessimulator.gameengine.gamestatemanager;

import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;

/**
 * Interface for game state managers.
 */
public interface IGameStateManager {
    /**
     * Gets the game entity state for the entity with the given id.
     * @param id The entity id.
     * @return The game entity state.
     */
    GameEntity getById(Integer id);

    /**
     * Adds a new entity state.
     * @param gameEntity The entity state to add.
     */
    void addEntityState(GameEntity gameEntity);

    /**
     * Removes the entity state for the given id.
     * @param entityId The entity id to remove.
     */
    void removeState(Integer entityId);
}
