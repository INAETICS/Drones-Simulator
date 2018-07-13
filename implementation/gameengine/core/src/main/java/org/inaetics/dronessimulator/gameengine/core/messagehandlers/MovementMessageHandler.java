package org.inaetics.dronessimulator.gameengine.core.messagehandlers;


import org.inaetics.dronessimulator.common.protocol.MovementMessage;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.IGameStateManager;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;

public class MovementMessageHandler implements MessageHandler<MovementMessage> {
    public MovementMessageHandler(IPhysicsEngineDriver physicsEngineDriver, IdentifierMapper id_mapper, IGameStateManager stateManager) {
        this.physicsEngineDriver = physicsEngineDriver;
        this.id_mapper = id_mapper;
        this.stateManager = stateManager;
    }

    /** The physics engine to update entities in. */
    private final IPhysicsEngineDriver physicsEngineDriver;

    /** The mapping between protocol and physics engine ids. */
    private final IdentifierMapper id_mapper;

    /** The game state manager for the entities. */
    private final IGameStateManager stateManager;

    @Override
    public void handleMessage(MovementMessage movementMessage) {
        // Change acceleration
        D3Vector acceleration = movementMessage.getAcceleration();
        if (acceleration != null) {
            physicsEngineDriver.changeAccelerationEntity(movementMessage.getIdentifier(), acceleration);
        }

        // Change direction
        D3PolarCoordinate direction = movementMessage.getDirection();
        if (direction != null) {
            physicsEngineDriver.changeDirectionEntity(movementMessage.getIdentifier(), direction);
        }

        // Change velocity
        D3Vector velocity = movementMessage.getVelocity();
        if (velocity != null) {
            physicsEngineDriver.changeVelocityEntity(movementMessage.getIdentifier(), velocity);
        }

    }
}
