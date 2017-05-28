package org.inaetics.dronessimulator.visualisation.messagehandlers;

import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;

public class FireBulletMessageHandler implements MessageHandler {
    @Override
    public void handleMessage(Message message) {
        // Bullet is added through statemessage
    }
}
