package org.inaetics.dronessimulator.gameengine.messagehandlers;


import lombok.AllArgsConstructor;
import org.inaetics.dronessimulator.common.protocol.MovementMessage;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.IGameStateManager;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;

@AllArgsConstructor
public class MovementMessageHandler implements MessageHandler<MovementMessage> {
    /** The physics engine to update entities in. */
    private final IPhysicsEngineDriver physicsEngineDriver;

    /** The mapping between protocol and physics engine ids. */
    private final IdentifierMapper id_mapper;

    /** The game state manager for the entities. */
    private final IGameStateManager stateManager;

    @Override
    public void handleMessage(MovementMessage movementMessage) {
        // Change acceleration
        movementMessage.getAcceleration().ifPresent(acceleration -> physicsEngineDriver.changeAccelerationEntity(movementMessage.getIdentifier(), acceleration));

        // Change direction
        movementMessage.getDirection().ifPresent(direction -> physicsEngineDriver.changeDirectionEntity(movementMessage.getIdentifier(), direction));

        // Change velocity
        movementMessage.getVelocity().ifPresent(velocity -> physicsEngineDriver.changeVelocityEntity(movementMessage.getIdentifier(), velocity));

    }
}
