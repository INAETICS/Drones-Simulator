package org.inaetics.dronessimulator.gameengine.test;

import lombok.Getter;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;

import java.util.concurrent.LinkedBlockingQueue;

@Getter
public class MockPhysicsEngineDriver implements IPhysicsEngineDriver{
    private final IdentifierMapper id_mapper;
    private GameEntity added = null;
    private int removed = -1;
    private int damaged = -1;
    private int damage = -1;

    private int moved = -1;
    private D3Vector newPosition = null;
    private D3Vector newVelocity = null;
    private D3Vector newAcceleration = null;

    public MockPhysicsEngineDriver(IdentifierMapper id_mapper) {
        this.id_mapper = id_mapper;
    }

    @Override
    public LinkedBlockingQueue<GameEngineEvent> getOutgoingQueue() {
        return null;
    }

    @Override
    public void addNewEntity(GameEntity entity, String protocolId) {
        this.added = entity;
    }

    @Override
    public void removeEntity(int entityId) {
        this.removed = entityId;
    }

    @Override
    public void removeEntity(String protocolId) {
        this.removeEntity(this.id_mapper.fromProtocolToGameEngineId(protocolId));
    }

    @Override
    public void damageEntity(int entityId, int damage) {
        this.damaged = entityId;
        this.damage = damage;
    }

    @Override
    public void damageEntity(String protocolId, int damage) {
        this.damageEntity(this.id_mapper.fromProtocolToGameEngineId(protocolId), damage);
    }

    @Override
    public void changePositionEntity(int entityId, D3Vector newPosition) {
        this.newPosition = newPosition;
        this.moved = entityId;
    }

    @Override
    public void changePositionEntity(String protocolId, D3Vector newPosition) {
        this.changePositionEntity(this.id_mapper.fromProtocolToGameEngineId(protocolId), newPosition);
    }

    @Override
    public void changeVelocityEntity(int entityId, D3Vector newVelocity) {
        this.newVelocity = newVelocity;
        this.moved = entityId;
    }

    @Override
    public void changeVelocityEntity(String protocolId, D3Vector newVelocity) {
        this.changeVelocityEntity(this.id_mapper.fromProtocolToGameEngineId(protocolId), newVelocity);
    }

    @Override
    public void changeAccelerationEntity(int entityId, D3Vector newAcceleration) {
        this.newAcceleration = newAcceleration;
        this.moved = entityId;
    }

    @Override
    public void changeAccelerationEntity(String protocolId, D3Vector newAcceleration) {
        this.changeAccelerationEntity(this.id_mapper.fromProtocolToGameEngineId(protocolId), newAcceleration);
    }
}
