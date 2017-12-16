package org.inaetics.dronessimulator.visualisation.messagehandlers;

import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.visualisation.BaseEntity;
import org.inaetics.dronessimulator.visualisation.Bullet;
import org.inaetics.dronessimulator.visualisation.Drone;
import org.inaetics.dronessimulator.visualisation.uiupdates.UIUpdate;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * The state message handler class. Implements what to do when a new state is send of the entities (drone, bullet)
 */
public class StateMessageHandler implements MessageHandler<StateMessage> {
    /** Logger */
    private static final Logger logger = Logger.getLogger(StateMessageHandler.class);
    /** UI updates */
    private final BlockingQueue<UIUpdate> uiUpdates;
    /** All the entities in the game */
    private final ConcurrentMap<String, BaseEntity> entities;

    /**
     * Instantiates a new state message handler
     * @param uiUpdates - ui updates
     * @param entities - entities
     */
    public StateMessageHandler(BlockingQueue<UIUpdate> uiUpdates, ConcurrentMap<String, BaseEntity> entities) {
        this.uiUpdates = uiUpdates;
        this.entities = entities;
    }

    /**
     * Creates a new bullet and returns it
     *
     * @param id String - Identifier of the new bullet
     * @return bullet Bullet - The newly created bullet
     */
    private Bullet createBullet(String id) {
        Bullet bullet = new Bullet(uiUpdates);
        bullet.setPosition(new D3Vector(0, 0, 0));
        bullet.setDirection(new D3PolarCoordinate(0, 0, 0));

        return bullet;
    }

    /**
     * Creates or updates a drone
     *
     * @param stateMessage - Message containing the state of the drone
     */
    private void updateDrone(StateMessage stateMessage) {
        Drone currentDrone = (Drone) entities.get(stateMessage.getIdentifier());
        if(currentDrone != null) {
            stateMessage.getPosition().ifPresent(currentDrone::setPosition);
            stateMessage.getDirection().ifPresent(currentDrone::setDirection);
            stateMessage.getHp().ifPresent(currentDrone::setCurrentHP);
        }
    }

    /**
     * Creates or updates a bullet
     *
     * @param stateMessage - Message containing the state of the bullet
     */
    private void createOrUpdateBullet(StateMessage stateMessage) {
        BaseEntity currentBullet = entities.computeIfAbsent(stateMessage.getIdentifier(), k -> createBullet(stateMessage.getIdentifier()));

        stateMessage.getPosition().ifPresent(currentBullet::setPosition);
        stateMessage.getDirection().ifPresent(currentBullet::setDirection);
    }

    /**
     * Updates a drone or creates and/or updates a bullet based on the message
     * @param stateMessage The received message.
     */
    @Override
    public void handleMessage(StateMessage stateMessage) {
        switch (stateMessage.getType()) {
            case DRONE:
                updateDrone(stateMessage);
                break;
            case BULLET:
                createOrUpdateBullet(stateMessage);
                break;
            default:
                logger.error("Received state message with unknown entity type! " + stateMessage);
        }
    }
}
