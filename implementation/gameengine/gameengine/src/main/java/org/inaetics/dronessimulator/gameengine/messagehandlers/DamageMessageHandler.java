package org.inaetics.dronessimulator.gameengine.messagehandlers;

import lombok.AllArgsConstructor;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.IGameStateManager;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.protocol.DamageMessage;
import org.inaetics.dronessimulator.pubsub.protocol.Message;

/**
 * Message handler for damage messages.
 */
@AllArgsConstructor
public class DamageMessageHandler implements MessageHandler {
    /** The physics engine to update entities in. */
    private final IPhysicsEngineDriver physicsEngineDriver;

    /** The mapping between protocol and physics engine ids. */
    private final IdentifierMapper id_mapper;

    /** The game state manager for the entities. */
    private final IGameStateManager stateManager;

    @Override
    public void handleMessage(Message message) {
        DamageMessage damageMessage = (DamageMessage) message;

        physicsEngineDriver.damageEntity(damageMessage.getEntityId(), damageMessage.getDamage());
    }
}
