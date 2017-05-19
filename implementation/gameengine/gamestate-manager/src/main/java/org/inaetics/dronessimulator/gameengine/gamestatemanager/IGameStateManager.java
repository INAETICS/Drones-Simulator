package org.inaetics.dronessimulator.gameengine.gamestatemanager;

import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;

public interface IGameStateManager {
    GameEntity getById(Integer id);

    void addEntityState(GameEntity gameEntity);

    void removeState(Integer entityId);
}
