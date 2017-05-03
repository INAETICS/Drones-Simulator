package org.inaetics.dronessimulator.gameengine.gamestatemanager;

import lombok.Getter;
import org.inaetics.dronessimulator.common.protocol.EntityType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;

@Getter
public class GameStateManager implements IGameStateManager {
    private final ConcurrentHashMap<Integer, GameEntity> state;


    public GameStateManager() {
        this.state = new ConcurrentHashMap<>();
    }


    public void addEntityState(GameEntity entity) {
        int id = entity.getEntityId();

        this.state.put(id, entity);
    }

    public void removeState(Integer entityId) {
        this.state.remove(entityId);
    }

    public EntityType getTypeFor(Integer id) {
        return this.state.get(id).getType();
    }

    public GameEntity getById(Integer id) {
        return this.state.get(id);
    }

    public List<GameEntity> getWithType(EntityType type) {
        return this.state.entrySet()
                         .stream()
                         .map(Map.Entry::getValue)
                         .filter((e) -> e.getType().equals(type))
                         .collect(Collectors.toList());
    }

    public void start() {
        Logger.getLogger(GameStateManager.class).info("Started GameState Manager!");
    }

    public void stop() {
        Logger.getLogger(GameStateManager.class).info("Stopped GameState Manager!");
    }
}
