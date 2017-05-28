package org.inaetics.dronessimulator.visualisation.messagehandlers;

import org.inaetics.dronessimulator.common.D3PoolCoordinate;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.visualisation.BaseEntity;
import org.inaetics.dronessimulator.visualisation.BasicDrone;
import org.inaetics.dronessimulator.visualisation.Bullet;
import org.inaetics.dronessimulator.visualisation.Drone;
import org.inaetics.dronessimulator.visualisation.controls.PannableCanvas;
import org.inaetics.dronessimulator.visualisation.uiupdates.UIUpdate;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;


public class StateMessageHandler implements MessageHandler {
    private final BlockingQueue<UIUpdate> uiUpdates;
    private final PannableCanvas canvas;
    private final ConcurrentMap<String, BaseEntity> entities;

    public StateMessageHandler(BlockingQueue<UIUpdate> uiUpdates, PannableCanvas canvas, ConcurrentMap<String, BaseEntity> entities) {
        this.uiUpdates = uiUpdates;
        this.canvas = canvas;
        this.entities = entities;
    }

    /**
     * Creates a new drone and returns it
     *
     * @param id String - Identifier of the new drone
     * @return drone Drone - The newly created drone
     */
    private Drone createPlayer(String id) {
        // create drone
        BasicDrone drone = new BasicDrone(uiUpdates, canvas);
        drone.setPosition(new D3Vector(500, 400, 1000));
        drone.setDirection(new D3PoolCoordinate(0, 0, 0));

        return drone;
    }

    /**
     * Creates a new bullet and returns it
     *
     * @param id String - Identifier of the new bullet
     * @return bullet Bullet - The newly created bullet
     */
    private Bullet createBullet(String id) {
        // create drone
        Bullet bullet = new Bullet(uiUpdates, canvas);

        bullet.setPosition(new D3Vector(0, 0, 0));
        bullet.setDirection(new D3PoolCoordinate(0, 0, 0));

        return bullet;
    }

    /**
     * Creates or updates a drone
     *
     * @param stateMessage - Message containing the state of the drone
     */
    private void createOrUpdateDrone(StateMessage stateMessage) {
        BaseEntity currentDrone = entities.computeIfAbsent(stateMessage.getIdentifier(), k -> createPlayer(stateMessage.getIdentifier()));

        if (stateMessage.getPosition().isPresent()) {
            currentDrone.setPosition(stateMessage.getPosition().get());
        }

        if (stateMessage.getDirection().isPresent()) {
            currentDrone.setDirection(stateMessage.getDirection().get());
        }
    }

    /**
     * Creates or updates a bullet
     *
     * @param stateMessage - Message containing the state of the bullet
     */
    private void createOrUpdateBullet(StateMessage stateMessage) {
        BaseEntity currentBullet = entities.computeIfAbsent(stateMessage.getIdentifier(), k -> createBullet(stateMessage.getIdentifier()));

        if (stateMessage.getPosition().isPresent()) {
            currentBullet.setPosition(stateMessage.getPosition().get());
        }

        if (stateMessage.getDirection().isPresent()) {
            currentBullet.setDirection(stateMessage.getDirection().get());
        }
    }

    @Override
    public void handleMessage(Message message) {
        StateMessage stateMessage = (StateMessage) message;

            switch (stateMessage.getType()) {
                case DRONE:
                    createOrUpdateDrone(stateMessage);
                    break;
                case BULLET:
                    createOrUpdateBullet(stateMessage);
                    break;
                default:
                    System.out.println("Unknown type");
            }
    }
}
