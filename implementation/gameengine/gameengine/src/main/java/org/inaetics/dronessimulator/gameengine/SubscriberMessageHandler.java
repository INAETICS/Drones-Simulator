package org.inaetics.dronessimulator.gameengine;


import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.*;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;

import java.util.Optional;

/**
 * Handle incoming pubsub commands to change acceleration and direction of an entity in the physicsengine.
 * Direction right now is ignored.
 */
@AllArgsConstructor
public class SubscriberMessageHandler implements MessageHandler {
    /**
     * Which physicsengine to update entities in
     */
    private final IPhysicsEngineDriver physicsEngineDriver;

    /**
     * Handle a message received from pubsub.
     * MovementMessages are used to change acceleration and direction of entity in engine
     * @param message The received message.
     */
    @Override
    public void handleMessage(Message message) {
        if(message instanceof MovementMessage) {
            // Change acceleration
            int entityId = 1;
            MovementMessage movementMessage = (MovementMessage) message;
            Optional<D3Vector> maybeAcceleration = movementMessage.getAcceleration();

            if(maybeAcceleration.isPresent()) {
                physicsEngineDriver.changeAccelerationEntity(entityId, maybeAcceleration.get());
            } else {
                Logger.getLogger(SubscriberMessageHandler.class).error("Received movement message without acceleration for drone " + entityId + ". Received: " + message);
            }

        } else if(message instanceof CollisionMessage) {
            // Do nothing

        } else if(message instanceof DamageMessage) {
            DamageMessage damageMessage = (DamageMessage) message;

            physicsEngineDriver.damageEntity(damageMessage.getEntityId(), damageMessage.getDamage());
        } else if(message instanceof KillMessage) {
            // Kill the entity
            KillMessage killMessage = (KillMessage) message;

            physicsEngineDriver.removeEntity(killMessage.getEntityId());

        } else {
            Logger.getLogger(SubscriberMessageHandler.class).error("Received a message which is not a movement message. Do not know what to do with it. Received: " + message.getClass().getName() + " " + message);
        }
    }
}
