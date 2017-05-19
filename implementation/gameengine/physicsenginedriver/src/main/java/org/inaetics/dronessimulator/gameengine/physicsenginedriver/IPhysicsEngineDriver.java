package org.inaetics.dronessimulator.gameengine.physicsenginedriver;


import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.gameentityupdate.GameEntityUpdate;

import java.util.concurrent.LinkedBlockingQueue;

public interface IPhysicsEngineDriver {
    LinkedBlockingQueue<GameEngineEvent> getOutgoingQueue();

    void addNewEntity(GameEntity entity);

    void removeEntity(int entityId);

    void damageEntity(int entityId, int damage);

    void changeAccelerationEntity(int entityId, D3Vector newAcceleration);
}
