package org.inaetics.dronessimulator.gameengine.physicsenginedriver;

import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.gameengine.common.gameevent.CollisionEndEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.CollisionStartEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.CurrentStateEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
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
public class PhysicsEngineObserver implements PhysicsEngineEventObserver {
    /** Event queue for game engine events. */
    private final LinkedBlockingQueue<GameEngineEvent> outgoingQueue;

    /** Game state manager to use. */
    private final IGameStateManager stateManager;

    /**
     * Creates an observer and send all events to the given queue.
     * @param outgoingQueue The queue to send events to.
     * @param stateManager The game state manager containing the game entities.
     */
    public PhysicsEngineObserver(LinkedBlockingQueue<GameEngineEvent> outgoingQueue, IGameStateManager stateManager) {
        this.outgoingQueue = outgoingQueue;
        this.stateManager = stateManager;
    }

    @Override
    public void collisionStartHandler(Entity e1, Entity e2) {
        GameEntity g1 = this.stateManager.getById(e1.getEntityId());
        GameEntity g2 = this.stateManager.getById(e2.getEntityId());

        if(g1 != null && g2 != null) {
            this.updateGameEntityFromPhysicsEngine(e1, g1);
            this.updateGameEntityFromPhysicsEngine(e2, g2);

            this.outgoingQueue.add(new CollisionStartEvent(g1.deepCopy(), g2.deepCopy()));
        }
    }

    @Override
    public void collisionStopHandler(Entity e1, Entity e2) {
        GameEntity g1 = this.stateManager.getById(e1.getEntityId());
        GameEntity g2 = this.stateManager.getById(e2.getEntityId());

        if(g1 != null && g2 != null) {
            this.updateGameEntityFromPhysicsEngine(e1, g1);
            this.updateGameEntityFromPhysicsEngine(e2, g2);

            this.outgoingQueue.add(new CollisionEndEvent(g1.deepCopy(), g2.deepCopy()));
        }
    }

    @Override
    public void broadcastStateHandler(List<Entity> currentState) {
        List<GameEntity> stateCopy = new ArrayList<>(currentState.size());

        for(Entity physicsEntity : currentState) {
            int id = physicsEntity.getEntityId();
            GameEntity gameEntity = this.stateManager.getById(id);

            if(gameEntity != null) {
                this.updateGameEntityFromPhysicsEngine(physicsEntity, gameEntity);

                stateCopy.add(gameEntity.deepCopy());
            }
        }

        this.outgoingQueue.add(new CurrentStateEvent(stateCopy));
    }

    /**
     * Updates the game entity with information from the physics engine.
     * @param physicsEntity The physics entity to use as source.
     * @param gameEntity The game entity to update.
     */
    private void updateGameEntityFromPhysicsEngine(Entity physicsEntity, GameEntity gameEntity) {
        if (physicsEntity.getEntityId() == gameEntity.getEntityId()) {
            gameEntity.setPosition(physicsEntity.getPosition());
            gameEntity.setVelocity(physicsEntity.getVelocity());
            gameEntity.setAcceleration(physicsEntity.getAcceleration());
            gameEntity.setDirection(physicsEntity.getDirection());
        } else {
            Logger.getLogger(GameEntity.class).fatal("Tried to update state from entity, but ids did not match. Received: " + physicsEntity.getEntityId() + ". Needed: " + gameEntity.getEntityId());
        }
    }

    public LinkedBlockingQueue<GameEngineEvent> getOutgoingQueue() {
        return outgoingQueue;
    }

    public IGameStateManager getStateManager() {
        return stateManager;
    }
}
