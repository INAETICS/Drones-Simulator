package org.inaetics.dronessimulator.gameengine.physicsenginedriver;

import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.IGameStateManager;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.gameentityupdate.GameEntityUpdate;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.GameStateManager;
import org.inaetics.dronessimulator.physicsengine.Entity;
import org.inaetics.dronessimulator.physicsengine.IPhysicsEngine;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.physicsengine.Size;

public class PhysicsEngineDriver implements IPhysicsEngineDriver {
    private transient volatile IPhysicsEngine m_physicsEngine;
    private transient volatile IGameStateManager m_stateManager;

    private final LinkedBlockingQueue<GameEngineEvent> outgoingQueue;
    private PhysicsEngineObserver engineObserver;

    public PhysicsEngineDriver() {
        this.outgoingQueue = new LinkedBlockingQueue<>();
    }

    public LinkedBlockingQueue<GameEngineEvent> getOutgoingQueue() {
        return this.outgoingQueue;
    }

    public void start() {
        Logger.getLogger(PhysicsEngineDriver.class).info("Starting PhysicsEngine Driver...");
        this.engineObserver = new PhysicsEngineObserver(this.outgoingQueue, m_stateManager);
        m_physicsEngine.setObserver(engineObserver);
        m_physicsEngine.setTimeBetweenBroadcastms(20L);

        Logger.getLogger(PhysicsEngineDriver.class).info("Started PhysicsEngine Driver!");
    }

    public void stop() {
        Logger.getLogger(PhysicsEngineDriver.class).info("Stopped PhysicsEngine Driver!");
    }

    public void addNewEntity(GameEntity gameEntity) {
        this.m_stateManager.addEntityState(gameEntity);
        this.m_physicsEngine.addInsert(PhysicsEngineDriver.gameEntityToPhysicsEntity(gameEntity));
    }

    public void addUpdate(int entityId, GameEntityUpdate update) {
        this.m_physicsEngine.addUpdate(entityId, update.toEntityUpdate());
    }

    public void addUpdates(int entityId, Collection<GameEntityUpdate> updates) {
        this.m_physicsEngine.addUpdates(entityId, updates.stream()
                                                         .map(GameEntityUpdate::toEntityUpdate)
                                                         .collect(Collectors.toList())
                                       );
    }

    public void removeEntity(int entityId) {
        this.m_physicsEngine.addRemoval(entityId);
        this.m_stateManager.removeState(entityId);
    }

    public void removeEntities(Collection<Integer> entityIds) {
        this.m_physicsEngine.addRemovals(entityIds);
        entityIds.forEach((id) -> this.m_stateManager.removeState(id));
    }

    private static Entity gameEntityToPhysicsEntity(GameEntity g) {
        return new Entity(g.getEntityId(), new Size(0.1,0.1,0.1), g.getPosition(), g.getVelocity(), g.getAcceleration());
    }
}
