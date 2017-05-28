package org.inaetics.dronessimulator.gameengine.physicsenginedriver;

import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.D3Vector;
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

import java.util.concurrent.LinkedBlockingQueue;


public class PhysicsEngineDriver implements IPhysicsEngineDriver {
    private transient volatile IPhysicsEngine m_physicsEngine;
    private transient volatile IGameStateManager m_stateManager;
    private transient volatile IdentifierMapper m_id_mapper;

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
        this.removeEntity(m_id_mapper.fromProtocolToGameEngineId(protocolId));
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
        this.damageEntity(m_id_mapper.fromProtocolToGameEngineId(protocolId), damage);
    }

    @Override
    public void changePositionEntity(int entityId, D3Vector newPosition) {
        this.m_physicsEngine.addUpdate(entityId, new PositionEntityUpdate(newPosition));
    }

    @Override
    public void changePositionEntity(String protocolId, D3Vector newPosition) {
        this.changePositionEntity(m_id_mapper.fromProtocolToGameEngineId(protocolId), newPosition);
    }

    @Override
    public void changeVelocityEntity(int entityId, D3Vector newVelocity) {
        this.m_physicsEngine.addUpdate(entityId, new VelocityEntityUpdate(newVelocity));
    }

    @Override
    public void changeVelocityEntity(String protocolId, D3Vector newVelocity) {
        this.changeVelocityEntity(m_id_mapper.fromProtocolToGameEngineId(protocolId), newVelocity);
    }

    @Override
    public void changeAccelerationEntity(int entityId, D3Vector newAcceleration) {
        this.m_physicsEngine.addUpdate(entityId, new AccelerationEntityUpdate(newAcceleration));
    }

    @Override
    public void changeAccelerationEntity(String protocolId, D3Vector newAcceleration) {
        this.changeAccelerationEntity(m_id_mapper.fromProtocolToGameEngineId(protocolId), newAcceleration);
    }

    private static Entity gameEntityToPhysicsEntity(GameEntity g) {
        return new Entity(g.getEntityId(), new Size(0.1,0.1,0.1), g.getPosition(), g.getVelocity(), g.getAcceleration());
    }
}
