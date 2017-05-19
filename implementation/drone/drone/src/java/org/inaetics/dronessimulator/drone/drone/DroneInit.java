package org.inaetics.dronessimulator.drone.drone;


import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import java.io.IOException;

public class DroneInit {
    private String identifier;
    private Subscriber m_subscriber;
    public String getIdentifier(){
        return identifier;
    }

    /**
     * FELIX CALLBACKS
     */
    public void start() {
        try {
            this.m_subscriber.addTopic(MessageTopic.STATEUPDATES);
        } catch (IOException e) {
            System.out.println("IO Exception add Topic");
        }
        this.m_subscriber.addHandler(KillMessage.class, this);
    }
    
    /**
     * -- MESSAGEHANDLERS
     */
    public void handleMessage(Message message) {
        if (message instanceof KillMessage){
            handleKillMessage((KillMessage) message);
        }
    }

    public void handleKillMessage(KillMessage killMessage){
        if(killMessage.getIdentifier().isPresent()){
            if(killMessage.getIdentifier().get().equals(this.getIdentifier())){
                // todo kill droneinit bundle.
            }
        }
    }


}
