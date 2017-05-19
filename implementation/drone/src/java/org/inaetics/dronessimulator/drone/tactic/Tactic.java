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
    protected volatile DroneInit m_drone;

    /**
     * -- Abstract metods
     */
    public abstract List<ServiceDependency> getComponents;
    public abstract D3Vector calculateTactics;


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
            this.calculateTactics();
        }
    }
}
