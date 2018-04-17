package org.inaetics.dronessimulator.gameengine.gamestatemanager;

import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;
import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Keeps the state of each game entity.
 */
public class GameStateManager implements IGameStateManager {
    @SuppressWarnings("unused") //Is set through OSGi
    private volatile ArchitectureEventController m_architectureEventController;

    /** State by id. */
    private final ConcurrentHashMap<Integer, GameEntity> state;

    /**
     * Instantiates a game state manager.
     */
    public GameStateManager() {
        this.state = new ConcurrentHashMap<>();
    }

    public ArchitectureEventController getM_architectureEventController() {
        return m_architectureEventController;
    }

    public ConcurrentHashMap<Integer, GameEntity> getState() {
        return state;
    }

    @Override
    public void addEntityState(GameEntity entity) {
        int id = entity.getEntityId();

        this.state.put(id, entity);
    }

    @Override
    public void removeState(Integer entityId) {
        this.state.remove(entityId);
    }

    /**
     * Gets the protocol type of the entity with the given id.
     * @param id The id of the entity.
     * @return The protocol type of the entity.
     */
    public EntityType getTypeFor(Integer id) {
        return this.state.get(id).getType();
    }

    @Override
    public GameEntity getById(Integer id) {
        return this.state.get(id);
    }

    /**
     * Gets the state of all entities with the given protocol type.
     * @param type The type to get the entity states for.
     * @return The entity states with the given type.
     */
    public List<GameEntity> getWithType(EntityType type) {
        return this.state.entrySet()
                         .stream()
                         .map(Map.Entry::getValue)
                         .filter((e) -> e.getType().equals(type))
                         .collect(Collectors.toList());
    }

    /**
     * Starts the game state manager service.
     */
    public void start() {
        m_architectureEventController.addHandler(SimulationState.INIT, SimulationAction.CONFIG, SimulationState.CONFIG, (SimulationState fromState, SimulationAction action, SimulationState toState) -> this.state.clear());

        Logger.getLogger(GameStateManager.class).info("Started GameState Manager!");
    }

    /**
     * Stops the game state manager service.
     */
    public void stop() {
        Logger.getLogger(GameStateManager.class).info("Stopped GameState Manager!");
    }
}
