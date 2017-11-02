package org.inaetics.dronessimulator.visualisation.messagehandlers;

import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.protocol.KillMessage;
import org.inaetics.dronessimulator.pubsub.protocol.Message;
import org.inaetics.dronessimulator.visualisation.BaseEntity;

import java.util.concurrent.ConcurrentMap;

/**
 * The kill message handler class. Implements what to do when an entity (bullet, drone) is killed/removed.
 */
public class KillMessageHandler implements MessageHandler {
    /** all the entities in the game */
    private final ConcurrentMap<String, BaseEntity> entities;

    /**
     * Instantiate the kill message handler
     * @param entities - entities in the game
     */
    public KillMessageHandler(ConcurrentMap<String, BaseEntity> entities) {
        this.entities = entities;
    }

    /**
     * Retrieve the entity from the message and then from the entitites map and then delete it
     * @param message The received message.
     */
    @Override
    public void handleMessage(Message message) {
        KillMessage killMessage = (KillMessage) message;
        BaseEntity baseEntity = entities.get(killMessage.getIdentifier());

        if(baseEntity != null) {
            baseEntity.delete();
            entities.remove(killMessage.getIdentifier());
        }
    }
}
