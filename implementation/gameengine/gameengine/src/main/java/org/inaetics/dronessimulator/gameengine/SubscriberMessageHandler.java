package org.inaetics.dronessimulator.gameengine;


import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.*;
import org.inaetics.dronessimulator.gameengine.common.state.Bullet;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.IGameStateManager;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IIdentifierMapper;
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

    private final IIdentifierMapper id_mapper;

    private final IGameStateManager stateManager;

    /**
     * Handle a message received from pubsub.
     * MovementMessages are used to change acceleration and direction of entity in engine
     * @param message The received message.
     */
    @Override
    public void handleMessage(Message message) {
        if(message instanceof MovementMessage) {
            // Change acceleration
            MovementMessage movementMessage = (MovementMessage) message;
            Optional<D3Vector> maybeAcceleration = movementMessage.getAcceleration();

            if(maybeAcceleration.isPresent()) {
                physicsEngineDriver.changeAccelerationEntity(id_mapper.fromProtocolToGameEngineId(movementMessage.getIdentifier()), maybeAcceleration.get());
            } else {
                Logger.getLogger(SubscriberMessageHandler.class).error("Received movement message without acceleration for drone " + movementMessage.getIdentifier() + ". Received: " + message);
            }

        } else if(message instanceof CollisionMessage) {
            // Do nothing

        } else if(message instanceof DamageMessage) {
            DamageMessage damageMessage = (DamageMessage) message;

            physicsEngineDriver.damageEntity(id_mapper.fromProtocolToGameEngineId(damageMessage.getEntityId()), damageMessage.getDamage());
        } else if(message instanceof KillMessage) {
            // Kill the entity
            KillMessage killMessage = (KillMessage) message;

            physicsEngineDriver.removeEntity(id_mapper.fromProtocolToGameEngineId(killMessage.getIdentifier()));
        } else if(message instanceof FireBulletMessage) {
            FireBulletMessage fireBulletMessage = (FireBulletMessage) message;


            int gameEngineId = id_mapper.getNewGameEngineId();

            Optional<D3Vector> maybePosition = fireBulletMessage.getPosition();
            Optional<D3Vector> maybeVelocity = fireBulletMessage.getVelocity();
            Optional<D3Vector> maybeAcceleration = fireBulletMessage.getAcceleration();


            if(maybePosition.isPresent() && maybeVelocity.isPresent() && maybeAcceleration.isPresent()) {
                GameEntity firedBy = stateManager.getById(id_mapper.fromProtocolToGameEngineId(fireBulletMessage.getFiredById()));
                Bullet bullet = new Bullet(gameEngineId, fireBulletMessage.getDamage(), firedBy, maybePosition.get(), maybeVelocity.get(), maybeAcceleration.get());

                id_mapper.setMapping(gameEngineId, fireBulletMessage.getIdentifier());
                physicsEngineDriver.addNewEntity(bullet);
            } else {
                Logger.getLogger(SubscriberMessageHandler.class).error("Received a fire bullet but the position, velocity or acceleraiton is not set: " + fireBulletMessage);
            }
        } else {
            Logger.getLogger(SubscriberMessageHandler.class).error("Received a message which is not a movement message. Do not know what to do with it. Received: " + message.getClass().getName() + " " + message);
        }
    }
}
