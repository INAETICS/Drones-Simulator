package org.inaetics.dronessimulator.gameengine.test;

import lombok.Getter;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;

import java.util.Optional;
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
        Optional<Integer> gameengineId = this.id_mapper.fromProtocolToGameEngineId(protocolId);

        if(gameengineId.isPresent()) {
            this.removeEntity(gameengineId.get());
        }
    }

    @Override
    public void damageEntity(int entityId, int damage) {
        this.damaged = entityId;
        this.damage = damage;
    }

    @Override
    public void damageEntity(String protocolId, int damage) {
        Optional<Integer> gameengineId = this.id_mapper.fromProtocolToGameEngineId(protocolId);

        if(gameengineId.isPresent()) {
            this.damageEntity(gameengineId.get(), damage);
        }
    }

    @Override
    public void changePositionEntity(int entityId, D3Vector newPosition) {
        this.newPosition = newPosition;
        this.moved = entityId;
    }

    @Override
    public void changePositionEntity(String protocolId, D3Vector newPosition) {
        Optional<Integer> gameengineId = this.id_mapper.fromProtocolToGameEngineId(protocolId);

        if(gameengineId.isPresent()) {
            this.changePositionEntity(gameengineId.get(), newPosition);
        }
    }

    @Override
    public void changeVelocityEntity(int entityId, D3Vector newVelocity) {
        this.newVelocity = newVelocity;
        this.moved = entityId;
    }

    @Override
    public void changeVelocityEntity(String protocolId, D3Vector newVelocity) {
        Optional<Integer> gameengineId = this.id_mapper.fromProtocolToGameEngineId(protocolId);

        if(gameengineId.isPresent()) {
            this.changeVelocityEntity(gameengineId.get(), newVelocity);
        }
    }

    @Override
    public void changeAccelerationEntity(int entityId, D3Vector newAcceleration) {
        this.newAcceleration = newAcceleration;
        this.moved = entityId;
    }

    @Override
    public void changeAccelerationEntity(String protocolId, D3Vector newAcceleration) {
        Optional<Integer> gameengineId = this.id_mapper.fromProtocolToGameEngineId(protocolId);

        if(gameengineId.isPresent()) {
            this.changeAccelerationEntity(gameengineId.get(), newAcceleration);
        }
    }
}
