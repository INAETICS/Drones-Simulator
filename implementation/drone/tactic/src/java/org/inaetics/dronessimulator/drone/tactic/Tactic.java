package org.inaetics.dronessimulator.drone.tactic;


import org.apache.felix.dm.DependencyManager;
import org.apache.felix.dm.ServiceDependency;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Tactic extends Thread {
    private volatile ArchitectureEventController m_architectureEventController;
    protected volatile DroneInit m_drone;

    private final int calculation_rate = 500;
    private final AtomicBoolean pauseToken = new AtomicBoolean(false);

    /**
     * Thread implementation
     */
    @Override
    public void run() {
        while(!this.isInterrupted()){
            this.calculateTactics();

            try{
                Thread.sleep(200);
            } catch(InterruptedException e){
                this.interrupt();
                e.printStackTrace();
            }

            synchronized (pauseToken) {
                while(pauseToken.get()) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        this.interrupt();
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void startTactic() {
        m_architectureEventController.addHandler(SimulationState.CONFIG, SimulationAction.START, SimulationState.RUNNING,
                (SimulationState fromState, SimulationAction action, SimulationState toState) -> {
                    this.startSimulation();
                }
        );

        m_architectureEventController.addHandler(SimulationState.RUNNING, SimulationAction.PAUSE, SimulationState.PAUSED,
                (SimulationState fromState, SimulationAction action, SimulationState toState) -> {
                    this.pauseSimulation();
                }
        );

        m_architectureEventController.addHandler(SimulationState.PAUSED, SimulationAction.RESUME, SimulationState.RUNNING,
                (SimulationState fromState, SimulationAction action, SimulationState toState) -> {
                    this.resumeSimulation();
                }
        );

        m_architectureEventController.addHandler(SimulationState.RUNNING, SimulationAction.STOP, SimulationState.STOPPED,
                (SimulationState fromState, SimulationAction action, SimulationState toState) -> {
                    this.stopSimulation();
                }
        );

        m_architectureEventController.addHandler(SimulationState.RUNNING, SimulationAction.GAMEOVER, SimulationState.GAMEOVER,
                (SimulationState fromState, SimulationAction action, SimulationState toState) -> {
                    this.stopSimulation();
                }
        );
    }

    @Override
    @Deprecated
    public void destroy() {
    }

    protected void startSimulation() {
        this.start();
    }

    protected void pauseSimulation() {
        synchronized (pauseToken) {
            pauseToken.set(true);
        }
    }

    protected void resumeSimulation() {
        synchronized (pauseToken) {
            pauseToken.set(false);
            pauseToken.notifyAll();
        }
    }

    protected void stopSimulation() {
        this.interrupt();
    }


    /**
     * -- Abstract metods
     */
    abstract void calculateTactics();

    public abstract Iterable<? extends ServiceDependency> getComponents(DependencyManager dependencyManager);
}
