package org.inaetics.dronessimulator.gameengine.core.messagehandlers;

import org.inaetics.dronessimulator.common.protocol.DamageMessage;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.IGameStateManager;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;

/**
 * Message handler for damage messages.
 */
public class DamageMessageHandler implements MessageHandler {
    public DamageMessageHandler(IPhysicsEngineDriver physicsEngineDriver, IdentifierMapper id_mapper, IGameStateManager stateManager) {
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
    public void handleMessage(Message message) {
        DamageMessage damageMessage = (DamageMessage) message;

        physicsEngineDriver.damageEntity(damageMessage.getEntityId(), damageMessage.getDamage());
    }
}
