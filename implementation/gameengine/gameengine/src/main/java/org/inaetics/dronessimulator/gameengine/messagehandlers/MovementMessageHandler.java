package org.inaetics.dronessimulator.gameengine.messagehandlers;


import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
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
    /**
     * Which physicsengine to update entities in
     */
    private final IPhysicsEngineDriver physicsEngineDriver;

    private final IdentifierMapper id_mapper;

    private final IGameStateManager stateManager;

    @Override
    public void handleMessage(Message message) {
        // Change acceleration
        MovementMessage movementMessage = (MovementMessage) message;
        Optional<D3Vector> maybeAcceleration = movementMessage.getAcceleration();

        if(maybeAcceleration.isPresent()) {
            physicsEngineDriver.changeAccelerationEntity(movementMessage.getIdentifier(),  maybeAcceleration.get());
        } else {
            Logger.getLogger(MovementMessageHandler.class).error("Received movement message without acceleration for drone " + movementMessage.getIdentifier() + ". Received: " + message);
        }
    }
}
