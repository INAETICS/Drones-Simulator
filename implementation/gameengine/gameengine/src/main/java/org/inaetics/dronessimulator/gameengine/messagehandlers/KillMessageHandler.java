package org.inaetics.dronessimulator.gameengine.messagehandlers;

import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.IGameStateManager;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;

@AllArgsConstructor
public class KillMessageHandler implements MessageHandler {
    private static final Logger logger = Logger.getLogger(KillMessageHandler.class);
    /** The physics engine to update entities in. */
    private final IPhysicsEngineDriver physicsEngineDriver;

    /** The mapping between protocol and physics engine ids. */
    private final IdentifierMapper id_mapper;

    /** The game state manager for the entities. */
    private final IGameStateManager stateManager;

    @Override
    public void handleMessage(Message message) {
        // Kill the entity
        KillMessage killMessage = (KillMessage) message;

        physicsEngineDriver.removeEntity(killMessage.getIdentifier());
        logger.info("Received killmessage for " + killMessage.getIdentifier() + " " + killMessage.getEntityType());
    }
}
