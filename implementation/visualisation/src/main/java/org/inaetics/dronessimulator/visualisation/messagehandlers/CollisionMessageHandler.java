package org.inaetics.dronessimulator.visualisation.messagehandlers;

import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;

/**
 * The message collision handler class. Implements what to do when a collision occurs.
 */
public class CollisionMessageHandler implements MessageHandler {
    /**
     * Empty method
     * @param message The received message.
     */
    @Override
    public void handleMessage(Message message) {
        // Do nothing on collision
    }
}
