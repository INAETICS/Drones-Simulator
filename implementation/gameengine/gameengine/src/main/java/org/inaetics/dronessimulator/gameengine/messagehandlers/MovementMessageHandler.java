package org.inaetics.dronessimulator.gameengine.messagehandlers;


import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.MovementMessage;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.IGameStateManager;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;

import java.util.Optional;

@AllArgsConstructor
public class MovementMessageHandler implements MessageHandler {
    /** The physics engine to update entities in. */
    private final IPhysicsEngineDriver physicsEngineDriver;

    /** The mapping between protocol and physics engine ids. */
    private final IdentifierMapper id_mapper;

    /** The game state manager for the entities. */
    private final IGameStateManager stateManager;

    @Override
    public void handleMessage(Message message) {
        // Change acceleration
        MovementMessage movementMessage = (MovementMessage) message;

        movementMessage.getAcceleration().ifPresent(acceleration -> physicsEngineDriver.changeAccelerationEntity(movementMessage.getIdentifier(), acceleration));
        movementMessage.getDirection().ifPresent(direction -> physicsEngineDriver.changeDirectionEntity(movementMessage.getIdentifier(), direction));
    }
}
