package org.inaetics.dronessimulator.gameengine.physicsenginedriver;

import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.IGameStateManager;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.gameentityupdate.AccelerationUpdate;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.gameentityupdate.DamageHealthEntityUpdate;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.gameentityupdate.GameEntityUpdate;
import org.inaetics.dronessimulator.physicsengine.Entity;
import org.inaetics.dronessimulator.physicsengine.IPhysicsEngine;
import org.inaetics.dronessimulator.physicsengine.Size;
import org.inaetics.dronessimulator.physicsengine.entityupdate.EntityUpdate;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

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

    private void addUpdate(int entityId, GameEntityUpdate update) {
        EntityUpdate physicsEngineUpdate = update.toPhysicsEngineEntityUpdate();

        // May be null as some updates do not do anything in the physicsengine
        if(physicsEngineUpdate != null) {
            this.m_physicsEngine.addUpdate(entityId, physicsEngineUpdate);
        }

        update.update(this.m_stateManager.getById(entityId));
    }

    private void addUpdates(int entityId, Collection<GameEntityUpdate> updates) {
        this.m_physicsEngine.addUpdates(entityId, updates.stream()
                                                         .map(GameEntityUpdate::toPhysicsEngineEntityUpdate)
                                                         // May be null as some updates do not do anything in the physicsengine
                                                         .filter(Objects::nonNull)
                                                         .collect(Collectors.toList())
                                       );
        updates.forEach((update) ->
            update.update(this.m_stateManager.getById(entityId))
        );
    }

    @Override
    public void damageEntity(int entityId, int damage) {
        this.addUpdate(entityId, new DamageHealthEntityUpdate(damage));
    }

    @Override
    public void changeAccelerationEntity(int entityId, D3Vector newAcceleration) {
        this.addUpdate(entityId, new AccelerationUpdate(newAcceleration));
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
