package org.inaetics.dronessimulator.gameengine.messagehandlers;

import lombok.AllArgsConstructor;
import org.inaetics.dronessimulator.common.protocol.DamageMessage;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.IGameStateManager;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;

@AllArgsConstructor
public class DamageMessageHandler implements MessageHandler {
    /**
     * Which physicsengine to update entities in
     */
    private final IPhysicsEngineDriver physicsEngineDriver;

    private final IdentifierMapper id_mapper;

    private final IGameStateManager stateManager;

    @Override
    public void handleMessage(Message message) {
        DamageMessage damageMessage = (DamageMessage) message;

        physicsEngineDriver.damageEntity(damageMessage.getEntityId(), damageMessage.getDamage());
    }
}
