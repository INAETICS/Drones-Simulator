package org.inaetics.dronessimulator.visualisation.messagehandlers;

import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.visualisation.BaseEntity;
import org.inaetics.dronessimulator.visualisation.Drone;

import java.util.concurrent.ConcurrentMap;


public class KillMessageHandler implements MessageHandler {
    private final ConcurrentMap<String, BaseEntity> entities;

    public KillMessageHandler(ConcurrentMap<String, BaseEntity> entities) {
        this.entities = entities;
    }

    @Override
    public void handleMessage(Message message) {
        KillMessage killMessage = (KillMessage) message;

        entities.get(killMessage.getIdentifier()).delete();
        entities.remove(killMessage.getIdentifier());
    }
}
