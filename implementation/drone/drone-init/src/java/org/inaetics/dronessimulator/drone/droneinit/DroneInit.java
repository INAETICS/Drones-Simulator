package org.inaetics.dronessimulator.drone.droneinit;


import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class DroneInit implements MessageHandler {
    private String identifier;
    private Subscriber m_subscriber;

    /**
     * FELIX CALLBACKS
     */
    public void start() {
        try {
            this.m_subscriber.addTopic(MessageTopic.STATEUPDATES);
        } catch (IOException e) {
            System.out.println("IO Exception add Topic");
        }
        this.m_subscriber.addHandler(StateMessage.class, this);


        // place indentifier in docker
    }

    public String getIdentifier(){
        if (this.identifier != null){
            return this.identifier;
        } else {
            Map<String, String> env = System.getenv();
            if(env.containsKey("DRONENAME"))
                return env.get("DRONENAME");
            else if (env.containsKey("COMPUTERNAME"))
                return env.get("COMPUTERNAME") + "-" + UUID.randomUUID().toString();
            else if (env.containsKey("HOSTNAME"))
                return env.get("HOSTNAME") + "-" + UUID.randomUUID().toString();
            else
                return UUID.randomUUID().toString();
        }
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
        if(killMessage.getIdentifier().equals(this.getIdentifier())){
            // todo kill droneinit bundle
        }
    }


}
