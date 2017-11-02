package org.inaetics.dronessimulator.visualisation.messagehandlers;

import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.protocol.Message;

/**
 * The fire bullet message handler class. Implements what to do when a bullet is fired.
 */
public class FireBulletMessageHandler implements MessageHandler {
    /**
     * Empty method
     * @param message The received message.
     */
    @Override
    public void handleMessage(Message message) {
        // Bullet is added through statemessage
    }
}
