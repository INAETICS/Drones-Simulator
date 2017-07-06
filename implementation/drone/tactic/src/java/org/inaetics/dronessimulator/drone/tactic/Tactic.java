package org.inaetics.dronessimulator.drone.tactic;


import org.apache.felix.dm.DependencyManager;
import org.apache.felix.dm.ServiceDependency;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;
import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.discovery.api.DuplicateName;
import org.inaetics.dronessimulator.discovery.api.Instance;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Group;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;


public abstract class Tactic extends Thread implements MessageHandler {
    private static final Logger logger = Logger.getLogger(Tactic.class);

    private volatile ArchitectureEventController m_architectureEventController;
    private volatile DroneInit m_drone;
    private volatile Subscriber m_subscriber;
    private volatile Discoverer m_discoverer;

    private final int calculation_rate = 500;

    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicBoolean quit = new AtomicBoolean(false);
    private final AtomicBoolean pauseToken = new AtomicBoolean(false);

    private Instance simulationInstance;
    private boolean registered = false;

    /**
     * Thread implementation
     */
    @Override
    public void run() {
        while(!this.isInterrupted()){
            try {
                //Wait for start
                synchronized (started) {
                    while(!started.get()) {
                        started.wait();
                    }
                }

                Logger.getLogger(Tactic.class).info("Started Tactic!");

                quit.set(false);
                pauseToken.set(false);

                // Work until quit
                while(!quit.get()) {
                    this.calculateTactics();

                    Thread.sleep(200);

                    synchronized (pauseToken) {
                        while(pauseToken.get()) {
                            pauseToken.wait();
                        }
                    }
                }

                Logger.getLogger(Tactic.class).info("Stopped Tactic!");

                started.set(false);
            } catch(InterruptedException e) {
                this.interrupt();
            }
        }
    }

    /**
     * Registers the handlers for the architectureEventController on startup. And registers the subscriber. Starts the tactic.
     */
    public void startTactic() {
        m_architectureEventController.addHandler(SimulationState.INIT, SimulationAction.CONFIG, SimulationState.CONFIG,
                (SimulationState fromState, SimulationAction action, SimulationState toState) -> {
                    this.configSimulation();
                }
        );

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


        m_architectureEventController.addHandler(SimulationState.CONFIG, SimulationAction.STOP, SimulationState.INIT,
                (SimulationState fromState, SimulationAction action, SimulationState toState) -> {
                    this.stopSimulation();
                }
        );

        m_architectureEventController.addHandler(SimulationState.RUNNING, SimulationAction.STOP, SimulationState.INIT,
                (SimulationState fromState, SimulationAction action, SimulationState toState) -> {
                    this.stopSimulation();
                }
        );

        m_architectureEventController.addHandler(SimulationState.PAUSED, SimulationAction.STOP, SimulationState.INIT,
                (SimulationState fromState, SimulationAction action, SimulationState toState) -> {
                    this.stopSimulation();
                }
        );

        m_architectureEventController.addHandler(SimulationState.RUNNING, SimulationAction.GAMEOVER, SimulationState.DONE,
                (SimulationState fromState, SimulationAction action, SimulationState toState) -> {
                    this.stopSimulation();
                }
        );

        simulationInstance = new Instance(Type.DRONE, Group.DRONE, m_drone.getIdentifier());

        registerSubscriber();

        super.start();
    }

    public void stopTactic() {
        unconfigSimulation();
    }

    private void registerSubscriber(){
        try {
            this.m_subscriber.addTopic(MessageTopic.STATEUPDATES);
        } catch (IOException e) {
            Logger.getLogger(Tactic.class).fatal(e);
        }
        this.m_subscriber.addHandler(KillMessage.class, this);
    }

    @Override
    public void destroy() {
        this.interrupt();
    }

    protected void configSimulation() {
        try {
            m_discoverer.register(simulationInstance);
            registered = true;
        } catch (IOException | DuplicateName e) {
            logger.fatal(e);
        }
    }

    protected void unconfigSimulation() {
        if(registered) {
            try {
                m_discoverer.unregister(simulationInstance);
                registered = false;
            } catch (IOException e) {
                logger.fatal(e);
            }
        }
    }

    /**
     * Start the simulation.
     */
    protected void startSimulation() {
        synchronized (started) {
            started.set(true);
            started.notifyAll();
        }

        logger.info("Started simulation!");
    }

    /**
     * Pauses the simulation.
     */
    protected void pauseSimulation() {
        synchronized (pauseToken) {
            pauseToken.set(true);
        }

        logger.info("Paused drone!");
    }

    /**
     * Resumes the simulation.
     */
    protected void resumeSimulation() {
        synchronized (pauseToken) {
            pauseToken.set(false);
            pauseToken.notifyAll();
        }

        logger.info("Resumed drone!");
    }

    /**
     * Stops the simulation.
     */
    protected void stopSimulation() {
        synchronized (quit) {
            quit.set(true);
            this.resumeSimulation();
        }

        unconfigSimulation();

        logger.info("Stopped drone!");
    }

    //-- MESSAGEHANDLERS

    /**
     * Handles a recieved message and calls the messagehandlers.
     * @param message The received message.
     */
    public void handleMessage(Message message) {
        if (message instanceof KillMessage){
            handleKillMessage((KillMessage) message);
        }
    }

    /**
     * Handles a killMessage
     * @param killMessage the received killMessage
     */
    public void handleKillMessage(KillMessage killMessage){
        if(killMessage.getIdentifier().equals(m_drone.getIdentifier())){
            Logger.getLogger(Tactic.class).info("Found kill message! Quitting for now...");
            this.stopSimulation();
        }
    }


    /**
     * -- Abstract metods
     */
    /**
     * Method which is called to calculate and perform the new tactics. A tactic should implement this method with its own logic.
     */
    abstract void calculateTactics();

    public abstract Iterable<? extends ServiceDependency> getComponents(DependencyManager dependencyManager);
}
