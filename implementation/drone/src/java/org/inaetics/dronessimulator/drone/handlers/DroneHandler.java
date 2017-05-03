package org.inaetics.dronessimulator.drone.handlers;

import org.inaetics.dronessimulator.drone.Drone;

/**
 * Created by mart on 1-5-17.
 */
public class DroneHandler extends Thread {
    private Drone drone;
    private boolean calculate = true;

    public DroneHandler(Drone d){
        drone = d;
    }

    public void stopCalculate(){
        this.calculate = false;
    }

    @Override
    public void run() {
        while(this.calculate){
            try{
                Thread.sleep(200);
            } catch(InterruptedException e){
                System.out.println(e);
            }
            drone.calculateTactics();
        }
    }
}
