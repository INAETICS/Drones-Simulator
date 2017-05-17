package org.inaetics.dronessimulator.drone.handlers;

import org.inaetics.dronessimulator.common.protocol.*;
import org.inaetics.dronessimulator.drone.Drone;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;

public class StateMessageHandler implements MessageHandler{
    private volatile Drone drone;

    public StateMessageHandler(Drone d){
        this.drone = d;
    }

    public void handleMessage(Message message) {
        if (message instanceof StateMessage){
            StateMessage stateMessage = (StateMessage) message;
            if (stateMessage.getIdentifier().equals(drone.getDroneId())){
                drone.setStateMessage(stateMessage);
            } else {

                drone.getRadar().getMessages().add(stateMessage);
            }
        }
    }

}