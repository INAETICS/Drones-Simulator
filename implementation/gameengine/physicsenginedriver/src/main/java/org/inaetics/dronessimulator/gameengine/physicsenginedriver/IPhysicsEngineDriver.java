package org.inaetics.dronessimulator.gameengine.physicsenginedriver;


import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;

import java.util.concurrent.LinkedBlockingQueue;

public interface IPhysicsEngineDriver {
    LinkedBlockingQueue<GameEngineEvent> getOutgoingQueue();

    void addNewEntity(GameEntity entity, String protocolId);

    void removeEntity(int entityId);
    void removeEntity(String protocolId);

    void damageEntity(int entityId, int damage);
    void damageEntity(String protocolId, int damage);

    void changePositionEntity(int entityId, D3Vector newPosition);
    void changePositionEntity(String protocolId, D3Vector newPosition);

    void changeVelocityEntity(int entityId, D3Vector newVelocity);
    void changeVelocityEntity(String protocolId, D3Vector newVelocity);

    void changeAccelerationEntity(int entityId, D3Vector newAcceleration);
    void changeAccelerationEntity(String protocolId, D3Vector newAcceleration);
}
