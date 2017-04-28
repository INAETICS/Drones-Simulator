package org.inaetics.dronessimulator.physicsenginewrapper;


import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.*;
import org.inaetics.dronessimulator.physicsengine.PhysicsEngine;
import org.inaetics.dronessimulator.physicsengine.entityupdate.AccelerationEntityUpdate;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;

import java.util.Optional;

/**
 * Handle incoming pubsub commands to change acceleration and direction of an entity in the physicsengine.
 * Direction right now is ignored.
 */
@AllArgsConstructor
public class DroneCommandMessageHandler implements MessageHandler {
    /**
     * Which physicsengine to update entities in
     */
    private final PhysicsEngine physicsEngine;

    /**
     * Handle a message received from pubsub.
     * MovementMessages are used to change acceleration and direction of entity in engine
     * @param message The received message.
     */
    @Override
    public void handleMessage(Message message) {
        if(message instanceof MovementMessage) {
            int entityId = 1;
            MovementMessage movementMessage = (MovementMessage) message;
            Optional<D3Vector> maybeAcceleration = movementMessage.getAcceleration();

            if(maybeAcceleration.isPresent()) {
                physicsEngine.addUpdate(entityId, new AccelerationEntityUpdate(maybeAcceleration.get()));
            } else {
                Logger.getLogger(DroneCommandMessageHandler.class).error("Received movement message without acceleration for drone " + entityId + ". Received: " + message);
            }

        } else {
            Logger.getLogger(DroneCommandMessageHandler.class).error("Received a message which is not a movement message. Do not know what to do with it. Received: " + message);
        }
    }
}
