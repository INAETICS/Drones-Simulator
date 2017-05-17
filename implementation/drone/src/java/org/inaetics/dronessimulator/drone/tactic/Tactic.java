package org.inaetics.dronessimulator.drone.tactic;

import org.apache.felix.dm.ServiceDependency;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.MovementMessage;
import org.inaetics.dronessimulator.drone.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;

import java.io.IOException;
import java.util.List;


public abstract class Tactic extends Thread{
    private volatile Publisher m_publisher;
    private volatile DroneInit m_drone;

    /**
     * -- Abstract metods
     */
    public abstract List<ServiceDependency> getComponents;
    public abstract D3Vector calculateTactics;


    /**
     *
     * @param acceleration
     */
    private synchronized void sendTactics(D3Vector acceleration){
        MovementMessage msg = new MovementMessage();
        msg.setAcceleration(acceleration);
        msg.setIdentifier(m_drone.getIdentifier());

        try {
            m_publisher.send(MessageTopic.MOVEMENTS, msg);
        } catch (IOException e) {
            System.out.println("Exception");
        }
    }

    /**
     * Thread implementation
     */
    @Override
    public void run() {
        while(true){
            try{
                Thread.sleep(200);
            } catch(InterruptedException e){
                System.out.println(e);
            }
            D3Vector new_accelartion  = this.calculateTactics();
            this.sendTactics(new_accelartion);
        }
    }
}
