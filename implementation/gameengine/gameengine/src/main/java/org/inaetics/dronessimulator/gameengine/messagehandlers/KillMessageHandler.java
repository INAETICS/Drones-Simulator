package org.inaetics.dronessimulator.gameengine.messagehandlers;

import lombok.AllArgsConstructor;
import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.IGameStateManager;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;

@AllArgsConstructor
public class KillMessageHandler implements MessageHandler {
    /**
     * Which physicsengine to update entities in
     */
    private final IPhysicsEngineDriver physicsEngineDriver;

    private final IdentifierMapper id_mapper;

    private final IGameStateManager stateManager;

    @Override
    public void handleMessage(Message message) {
        // Kill the entity
        KillMessage killMessage = (KillMessage) message;

        physicsEngineDriver.removeEntity(killMessage.getIdentifier());
    }
}
