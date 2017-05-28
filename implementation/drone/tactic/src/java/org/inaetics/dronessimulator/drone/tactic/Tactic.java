package org.inaetics.dronessimulator.drone.tactic;

import org.apache.felix.dm.DependencyManager;
import org.apache.felix.dm.ServiceDependency;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;

public abstract class Tactic extends Thread{
    protected volatile DroneInit m_drone;
    private final int calculation_rate = 500;




    /**
     * -- Abstract metods
     */
    abstract void calculateTactics();


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

    public abstract Iterable<? extends ServiceDependency> getComponents(DependencyManager dependencyManager);
}
