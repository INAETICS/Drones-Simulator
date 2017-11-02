package org.inaetics.dronessimulator.gameengine.messagehandlers;

import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.gameengine.common.state.Bullet;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.IGameStateManager;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.protocol.EntityType;
import org.inaetics.dronessimulator.pubsub.protocol.FireBulletMessage;
import org.inaetics.dronessimulator.pubsub.protocol.Message;

import java.util.Optional;

@AllArgsConstructor
public class FireBulletMessageHandler implements MessageHandler {
    /** The physics engine to update entities in. */
    private final IPhysicsEngineDriver physicsEngineDriver;

    /** The mapping between protocol and physics engine ids. */
    private final IdentifierMapper id_mapper;

    /** The game state manager for the entities. */
    private final IGameStateManager stateManager;

    @Override
    public void handleMessage(Message message) {
        FireBulletMessage fireBulletMessage = (FireBulletMessage) message;

        int gameEngineId = id_mapper.getNewGameEngineId();

        Optional<D3Vector> maybePosition = fireBulletMessage.getPosition();
        Optional<D3Vector> maybeVelocity = fireBulletMessage.getVelocity();
        Optional<D3Vector> maybeAcceleration = fireBulletMessage.getAcceleration();
        Optional<D3PolarCoordinate> maybeDirection = fireBulletMessage.getDirection();


        if(fireBulletMessage.getType().equals(EntityType.BULLET) && maybePosition.isPresent() && maybeVelocity.isPresent() && maybeAcceleration.isPresent() && maybeDirection.isPresent()) {
            Optional<Integer> maybeGameengineId = id_mapper.fromProtocolToGameEngineId(fireBulletMessage.getFiredById());

            if(maybeGameengineId.isPresent()) {
                GameEntity firedBy = stateManager.getById(maybeGameengineId.get());
                Bullet bullet = new Bullet(gameEngineId, fireBulletMessage.getDamage(), firedBy, maybePosition.get(), maybeVelocity.get(), maybeAcceleration.get(), maybeDirection.get());

                physicsEngineDriver.addNewEntity(bullet, fireBulletMessage.getIdentifier());
            }
        } else {
            Logger.getLogger(FireBulletMessageHandler.class).error("Received a fire bullet but the type, position, velocity or acceleration is not set: " + fireBulletMessage);
        }
    }
}
