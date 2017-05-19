package org.inaetics.dronessimulator.gameengine.physicsenginedriver;


import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.gameentityupdate.GameEntityUpdate;

import java.util.concurrent.LinkedBlockingQueue;

public interface IPhysicsEngineDriver {
    LinkedBlockingQueue<GameEngineEvent> getOutgoingQueue();

    void addNewEntity(GameEntity entity);

    void addUpdate(int entityId, GameEntityUpdate update);
}
