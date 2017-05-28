package org.inaetics.dronessimulator.visualisation.messagehandlers;

import javafx.scene.layout.Pane;
import org.inaetics.dronessimulator.common.D3PoolCoordinate;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.visualisation.BasicDrone;
import org.inaetics.dronessimulator.visualisation.Drone;

import java.util.concurrent.ConcurrentMap;


public class StateMessageHandler implements MessageHandler {
    private final Pane playfieldLayer;
    private final ConcurrentMap<String, Drone> drones;

    public StateMessageHandler(Pane playfieldLayer, ConcurrentMap<String, Drone> drones) {
        this.playfieldLayer = playfieldLayer;
        this.drones = drones;
    }

    /**
     * Creates a new drone and returns it
     *
     * @param id String - Identifier of the new drone
     * @return drone Drone - The newly created drone
     */
    private Drone createPlayer(String id) {
        // create drone
        BasicDrone drone = new BasicDrone(playfieldLayer);

        drone.setPosition(new D3Vector(0, 0, 0));
        drone.setDirection(new D3PoolCoordinate(0, 0, 0));

        return drone;
    }

    @Override
    public void handleMessage(Message message) {
        StateMessage stateMessage = (StateMessage) message;

        Drone currentDrone = drones.computeIfAbsent(stateMessage.getIdentifier(), k -> createPlayer(stateMessage.getIdentifier()));

        if (stateMessage.getPosition().isPresent()) {
            currentDrone.setPosition(stateMessage.getPosition().get());
        }

        if (stateMessage.getDirection().isPresent()) {
            currentDrone.setDirection(stateMessage.getDirection().get());
        }
    }
}
