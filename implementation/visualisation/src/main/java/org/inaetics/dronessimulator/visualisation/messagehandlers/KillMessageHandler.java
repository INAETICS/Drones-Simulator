package org.inaetics.dronessimulator.visualisation.messagehandlers;

import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.visualisation.Drone;

import java.util.concurrent.ConcurrentMap;


public class KillMessageHandler implements MessageHandler {
    private final ConcurrentMap<String, Drone> drones;

    public KillMessageHandler(ConcurrentMap<String, Drone> drones) {
        this.drones = drones;
    }

    @Override
    public void handleMessage(Message message) {
        KillMessage killMessage = (KillMessage) message;

        // todo: add boolean remove to drone. When this boolean is set, then do explosion animation and remove drone.
        drones.remove(killMessage.getIdentifier());
    }
}
