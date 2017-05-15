package org.inaetics.dronessimulator.gameengine.physicsenginedriver;

import lombok.Getter;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.gameengine.common.gameevent.CollisionEndEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.CollisionStartEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.CurrentStateEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.GameStateManager;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.IGameStateManager;
import org.inaetics.dronessimulator.physicsengine.Entity;
import org.inaetics.dronessimulator.physicsengine.PhysicsEngineEventObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * An observer for the {@link org.inaetics.dronessimulator.physicsengine.PhysicsEngine}.
 * Wraps any events into a {@link org.inaetics.dronessimulator.gameengine.common.gameevent} message
 * and puts it into the outgoingQueue for further processing.
 */
@Getter
public class PhysicsEngineObserver implements PhysicsEngineEventObserver {
    private final LinkedBlockingQueue<GameEngineEvent> outgoingQueue;
    private final IGameStateManager stateManager;

    /**
     * Create an observer and send all events to the outgoingQueue
     * @param outgoingQueue Send all events to this queue
     */
    public PhysicsEngineObserver(LinkedBlockingQueue<GameEngineEvent> outgoingQueue, IGameStateManager stateManager) {
        this.outgoingQueue = outgoingQueue;
        this.stateManager = stateManager;
    }

    /**
     * How to handle a collision start event. Send to outgoingqueue.
     * @param e1 First entity in the collision
     * @param e2 Second entity in the collision
     */
    @Override
    public void collisionStartHandler(Entity e1, Entity e2) {
        GameEntity g1 = this.stateManager.getById(e1.getId());
        GameEntity g2 = this.stateManager.getById(e2.getId());

        this.updateGameEntityFromPhysicsEngine(e1, g1);
        this.updateGameEntityFromPhysicsEngine(e2, g2);

        this.outgoingQueue.add(new CollisionStartEvent(g1.deepCopy(), g2.deepCopy()));
    }

    /**
     * How to handle a collision stop event. Send to outgoingqueue.
     * @param e1 First entity in the ended collision
     * @param e2 Second entity in the ended collision
     */
    @Override
    public void collisionStopHandler(Entity e1, Entity e2) {
        GameEntity g1 = this.stateManager.getById(e1.getId());
        GameEntity g2 = this.stateManager.getById(e2.getId());

        this.updateGameEntityFromPhysicsEngine(e1, g1);
        this.updateGameEntityFromPhysicsEngine(e2, g2);

        this.outgoingQueue.add(new CollisionEndEvent(g1.deepCopy(), g2.deepCopy()));
    }

    /**
     * How to handle a broadcast state event. Send to outgoingqueue
     * @param currentState All information about all entities. Deepcopy so no link to state in physicsengine.
     */
    @Override
    public void broadcastStateHandler(List<Entity> currentState) {
        List<GameEntity> stateCopy = new ArrayList<>(currentState.size());

        for(Entity physicsEntity : currentState) {
            int id = physicsEntity.getId();
            GameEntity gameEntity = this.stateManager.getById(id);

            if(gameEntity != null) {
                this.updateGameEntityFromPhysicsEngine(physicsEntity, gameEntity);

                stateCopy.add(gameEntity.deepCopy());
            } else {
                Logger.getLogger(GameStateManager.class).fatal("Tried to update complete state. Found entity in engine which is not in state. Engine id: " + id);
            }

        }

        this.outgoingQueue.add(new CurrentStateEvent(stateCopy));
    }

    /**
     * Update the game entity with information from the physics engine
     * @param physicsEntity Which physicsengine entity to use as source of information
     * @param gameEntity
     */
    private void updateGameEntityFromPhysicsEngine(Entity physicsEntity, GameEntity gameEntity) {
        if(physicsEntity.getId() == gameEntity.getEntityId()) {
            gameEntity.setPosition(physicsEntity.getPosition());
            gameEntity.setVelocity(physicsEntity.getVelocity());
            gameEntity.setAcceleration(physicsEntity.getAcceleration());
        } else {
            Logger.getLogger(GameEntity.class).fatal("Tried to update state from entity, but ids did not match. Received: " + physicsEntity.getId() + ". Needed: " + gameEntity.getEntityId());
        }
    }
}
