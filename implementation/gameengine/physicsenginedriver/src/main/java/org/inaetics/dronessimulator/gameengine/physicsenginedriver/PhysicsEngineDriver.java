package org.inaetics.dronessimulator.gameengine.physicsenginedriver;

import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventHandler;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;
import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.common.state.HealthGameEntity;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.IGameStateManager;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.physicsengine.Entity;
import org.inaetics.dronessimulator.physicsengine.IPhysicsEngine;
import org.inaetics.dronessimulator.physicsengine.Size;
import org.inaetics.dronessimulator.physicsengine.entityupdate.AccelerationEntityUpdate;
import org.inaetics.dronessimulator.physicsengine.entityupdate.PositionEntityUpdate;
import org.inaetics.dronessimulator.physicsengine.entityupdate.VelocityEntityUpdate;

import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Driver for the simple physics engine shipped with the game engine.
 */
public class PhysicsEngineDriver implements IPhysicsEngineDriver {
    private final static Logger logger = Logger.getLogger(PhysicsEngineDriver.class);

    /** The physics engine instance to use. */
    private transient volatile IPhysicsEngine m_physicsEngine;

    /** The game state manager to use. */
    private transient volatile IGameStateManager m_stateManager;

    /** The identifier mapper to use. */
    private transient volatile IdentifierMapper m_id_mapper;
    private transient volatile ArchitectureEventController m_architectureEventController;

    /** Event queue for events produced by the physics engine. */
    private final LinkedBlockingQueue<GameEngineEvent> outgoingQueue;

    /** Observer for the physics engine. */
    private PhysicsEngineObserver engineObserver;

    /**
     * Instantiates a new physics engine driver instance.
     */
    public PhysicsEngineDriver() {
        this.outgoingQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public LinkedBlockingQueue<GameEngineEvent> getOutgoingQueue() {
        return this.outgoingQueue;
    }

    /**
     * Starts the physics engine driver.
     */
    public void start() {
        Logger.getLogger(PhysicsEngineDriver.class).info("Starting PhysicsEngine Driver...");
        this.engineObserver = new PhysicsEngineObserver(this.outgoingQueue, m_stateManager);
        m_physicsEngine.setObserver(engineObserver);
        m_physicsEngine.setTimeBetweenBroadcastms(20L);

        m_architectureEventController.addHandler(SimulationState.CONFIG, SimulationAction.START, SimulationState.RUNNING, (SimulationState fromState, SimulationAction action, SimulationState toState) -> {
            logger.info("Starting simulation!");
            this.startEngine();
        });

        m_architectureEventController.addHandler(SimulationState.PAUSED, SimulationAction.RESUME, SimulationState.RUNNING, (SimulationState fromState, SimulationAction action, SimulationState toState) -> {
            logger.info("Resuming simulation!");
            this.resumeEngine();
        });

        m_architectureEventController.addHandler(SimulationState.RUNNING, SimulationAction.PAUSE, SimulationState.PAUSED, (SimulationState fromState, SimulationAction action, SimulationState toState) -> {
            logger.info("Pausing simulation!");
            this.pauseEngine();
        });

        ArchitectureEventHandler stopHandler = (SimulationState fromState, SimulationAction action, SimulationState toState) -> {
            logger.info("Stopping simulation!");
            this.stopEngine();
        };

        m_architectureEventController.addHandler(SimulationState.RUNNING, SimulationAction.GAMEOVER, SimulationState.DONE, stopHandler);
        m_architectureEventController.addHandler(SimulationState.RUNNING, SimulationAction.STOP, SimulationState.INIT, stopHandler);
        m_architectureEventController.addHandler(SimulationState.CONFIG, SimulationAction.STOP, SimulationState.INIT, stopHandler);
        m_architectureEventController.addHandler(SimulationState.PAUSED, SimulationAction.STOP, SimulationState.INIT, stopHandler);

        logger.info("Started PhysicsEngine Driver!");
    }

    /**
     * Stops the physics engine driver.
     */
    public void stop() {
        logger.info("Stopped PhysicsEngine Driver!");
    }

    @Override
    public void addNewEntity(GameEntity gameEntity, String protocolId) {
        this.m_stateManager.addEntityState(gameEntity);
        this.m_physicsEngine.addInsert(PhysicsEngineDriver.gameEntityToPhysicsEntity(gameEntity));

        this.m_id_mapper.setMapping(gameEntity.getEntityId(), protocolId);
    }

    @Override
    public void removeEntity(int entityId) {
        this.m_physicsEngine.addRemoval(entityId);
        this.m_stateManager.removeState(entityId);
        this.m_id_mapper.removeMapping(entityId);
    }

    @Override
    public void removeEntity(String protocolId) {
        Optional<Integer> gameEngineId = m_id_mapper.fromProtocolToGameEngineId(protocolId);
        if(gameEngineId.isPresent()) {
            this.removeEntity(gameEngineId.get());
        }
    }

    @Override
    public void damageEntity(int entityId, int damage) {
        GameEntity e = this.m_stateManager.getById(entityId);

        if(e != null) {
            if(e instanceof HealthGameEntity) {
                HealthGameEntity healthGameEntity = (HealthGameEntity) e;

                healthGameEntity.damage(damage);
            } else {
                Logger.getLogger(PhysicsEngineDriver.class).error("Tried to damage an entity without hp! Got: " + entityId + " " + e);
            }
        } else {
            // It is possible that an entity is removed but something damages the entity just before it is removed.
        }
    }

    @Override
    public void damageEntity(String protocolId, int damage) {
        Optional<Integer> gameEngineId = m_id_mapper.fromProtocolToGameEngineId(protocolId);
        if(gameEngineId.isPresent()) {
            this.damageEntity(gameEngineId.get(), damage);
        }
    }

    @Override
    public void changePositionEntity(int entityId, D3Vector newPosition) {
        this.m_physicsEngine.addUpdate(entityId, new PositionEntityUpdate(newPosition));
    }

    @Override
    public void changePositionEntity(String protocolId, D3Vector newPosition) {
        Optional<Integer> gameEngineId = m_id_mapper.fromProtocolToGameEngineId(protocolId);
        if(gameEngineId.isPresent()) {
            this.changePositionEntity(gameEngineId.get(), newPosition);
        }
    }

    @Override
    public void changeVelocityEntity(int entityId, D3Vector newVelocity) {
        this.m_physicsEngine.addUpdate(entityId, new VelocityEntityUpdate(newVelocity));
    }

    @Override
    public void changeVelocityEntity(String protocolId, D3Vector newVelocity) {
        Optional<Integer> gameEngineId = m_id_mapper.fromProtocolToGameEngineId(protocolId);
        if(gameEngineId.isPresent()) {
            this.changeVelocityEntity(gameEngineId.get(), newVelocity);
        }
    }

    @Override
    public void changeAccelerationEntity(int entityId, D3Vector newAcceleration) {
        this.m_physicsEngine.addUpdate(entityId, new AccelerationEntityUpdate(newAcceleration));
    }

    @Override
    public void changeAccelerationEntity(String protocolId, D3Vector newAcceleration) {
        Optional<Integer> gameEngineId = m_id_mapper.fromProtocolToGameEngineId(protocolId);
        if(gameEngineId.isPresent()) {
            this.changeAccelerationEntity(gameEngineId.get(), newAcceleration);
        }
    }

    @Override
    public void startEngine() {
        m_physicsEngine.startEngine();
    }

    @Override
    public void pauseEngine() {
        m_physicsEngine.pauseEngine();
    }

    @Override
    public void resumeEngine() {
        m_physicsEngine.resumeEngine();
    }

    @Override
    public void stopEngine() {
        m_physicsEngine.stopEngine();
    }

    /**
     * Converts a game entity to a physics engine entity.
     * @param g The game entity.
     * @return The new physics engine entity.
     */
    private static Entity gameEntityToPhysicsEntity(GameEntity g) {
        Size size;

        if(g.getType().equals(EntityType.DRONE)) {
            size = new Size(10, 10, 10);
        } else if(g.getType().equals(EntityType.BULLET)) {
            size = new Size(1, 1, 1);
        } else {
            size = new Size(100, 100, 100);
        }

        return new Entity(g.getEntityId(), size, g.getPosition(), g.getVelocity(), g.getAcceleration());
    }
}