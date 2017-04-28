package org.inaetics.dronessimulator.drone;

import org.inaetics.dronessimulator.common.protocol.*;
import org.inaetics.dronessimulator.common.*;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.publisher.*;
import org.inaetics.dronessimulator.pubsub.api.subscriber.*;

public class StateMessageHandler implements MessageHandler{

    public void handleMessage(Message message) {
        System.out.println("Position retrieved.");
        if (message instanceof StateMessage){
            StateMessage stateMessage = (StateMessage) message;
            //if (stateMessage.getPosition().isPresent()) this.setPosition(stateMessage.getPosition().get());
            //if (stateMessage.getDirection().isPresent()) this.setDirection(stateMessage.getDirection().get());
            //if (stateMessage.getAcceleration().isPresent()) this.setAcceleration(stateMessage.getAcceleration().get());
            //this.calculateTactics();
        }
    }

}