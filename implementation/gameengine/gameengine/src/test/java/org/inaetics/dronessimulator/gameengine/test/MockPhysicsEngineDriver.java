package org.inaetics.dronessimulator.gameengine.test;


import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.gameentityupdate.GameEntityUpdate;

import java.util.concurrent.LinkedBlockingQueue;

public class MockPhysicsEngineDriver implements IPhysicsEngineDriver{
    private final LinkedBlockingQueue<GameEntity> incomingAddQueue;
    private final LinkedBlockingQueue<GameEntityUpdate> incomingUpdateQueue;
    private final LinkedBlockingQueue<GameEngineEvent> outgoingQueue;


    public MockPhysicsEngineDriver() {
        this.incomingAddQueue = new LinkedBlockingQueue<>();
        this.incomingUpdateQueue = new LinkedBlockingQueue<>();
        this.outgoingQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public LinkedBlockingQueue<GameEngineEvent> getOutgoingQueue() {
        return this.outgoingQueue;
    }

    @Override
    public void addNewEntity(GameEntity entity) {
        this.incomingAddQueue.add(entity);
    }

    @Override
    public void addUpdate(int entityId, GameEntityUpdate update) {
        this.incomingUpdateQueue.add(update);
    }

    public LinkedBlockingQueue<GameEntityUpdate> getIncomingUpdateQueue() {
        return incomingUpdateQueue;
    }

    public LinkedBlockingQueue<GameEntity> getIncomingAddQueue() {
        return incomingAddQueue;
    }
}
