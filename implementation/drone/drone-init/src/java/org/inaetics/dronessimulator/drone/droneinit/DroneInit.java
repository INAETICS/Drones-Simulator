package org.inaetics.dronessimulator.drone.droneinit;


import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.discovery.api.DuplicateName;
import org.inaetics.dronessimulator.discovery.api.Instance;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Group;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DroneInit implements MessageHandler {
    private String identifier;
    private Subscriber m_subscriber;
    private Discoverer m_discoverer;
    private Instance registered_instance;

    public DroneInit(){
        this.initIdentifier();
    }

    /**
     * FELIX CALLBACKS
     */
    public void start() throws IOException{
        this.registerSubscriber();
        this.registerDrone();
    }

    public void stop() throws IOException {
        this.unregisterDrone();
    }

    private void registerSubscriber(){
        try {
            this.m_subscriber.addTopic(MessageTopic.STATEUPDATES);
        } catch (IOException e) {
            System.out.println("IO Exception add Topic");
        }
        this.m_subscriber.addHandler(StateMessage.class, this);
    }

    private void registerDrone() throws IOException{
        Map<String, String> properties = new HashMap<>();

        properties.put("team", "team1");
        Instance instance = new Instance(Type.DRONE, Group.DRONE, this.getIdentifier(), properties, true);
        try{
            m_discoverer.register(instance);
            this.registered_instance = instance;
        } catch(DuplicateName e){
            this.setIdentifier(this.getIdentifier() + "-" + UUID.randomUUID().toString());
            this.registerDrone();
        }

    }

    private void unregisterDrone() throws IOException{
        this.m_discoverer.unregister(registered_instance);
    }

    public String getIdentifier(){
        return this.identifier;
    }

    public void setIdentifier(String new_identifier){
        this.identifier = new_identifier;
    }

    public void initIdentifier(){
        Map<String, String> env = System.getenv();
        if(env.containsKey("DRONENAME"))
            this.setIdentifier(env.get("DRONENAME"));
        else if (env.containsKey("COMPUTERNAME"))
            this.setIdentifier(env.get("COMPUTERNAME"));
        else if (env.containsKey("HOSTNAME"))
            this.setIdentifier(env.get("HOSTNAME"));
        else
            this.setIdentifier(UUID.randomUUID().toString());
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
            throw new RuntimeException("GAMEOVER! - Drone is killed!");
        }
    }


}