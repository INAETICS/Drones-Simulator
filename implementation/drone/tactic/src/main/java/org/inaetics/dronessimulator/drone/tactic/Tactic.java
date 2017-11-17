package org.inaetics.dronessimulator.drone.tactic;


import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.common.ManagedThread;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;
import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.discovery.api.DuplicateName;
import org.inaetics.dronessimulator.discovery.api.Instance;
import org.inaetics.dronessimulator.discovery.api.instances.TacticInstance;
import org.inaetics.dronessimulator.drone.components.engine.Engine;
import org.inaetics.dronessimulator.drone.components.gps.GPS;
import org.inaetics.dronessimulator.drone.components.gun.Gun;
import org.inaetics.dronessimulator.drone.components.radar.Radar;
import org.inaetics.dronessimulator.drone.components.radio.Radio;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The abstract tactic each drone tactic should extend
 */
@Log4j
public abstract class Tactic extends ManagedThread implements MessageHandler {

    // drone components
    protected volatile Radar radar;
    protected volatile GPS gps;
    protected volatile Engine engine;
    protected volatile Gun gun;
    protected volatile Radio radio;
    /**
     * Architecture Event controller bundle
     */
    private volatile ArchitectureEventController m_architectureEventController;
    /**
     * Drone Init bundle
     */
    private volatile DroneInit m_drone;
    /**
     * Subscriber bundle
     */
    private volatile Subscriber m_subscriber;
    /**
     * Discoverer bundle
     */
    private volatile Discoverer m_discoverer;
    private Instance simulationInstance;
    private boolean registered = false;
    private AtomicBoolean initialized = new AtomicBoolean(false);

    /**
     * Thread implementation
     */
    @Override
    protected final void work() throws InterruptedException {
        this.calculateTactics();
        Thread.sleep(200);
    }

    /**
     * Registers the handlers for the architectureEventController on startup. And registers the subscriber. Starts the
     * tactic.
     */
    public final void startTactic() {
        m_architectureEventController.addHandler(SimulationState.INIT, SimulationAction.CONFIG, SimulationState.CONFIG,
                (SimulationState fromState, SimulationAction action, SimulationState toState) -> this.configSimulation()
        );

        m_architectureEventController.addHandler(SimulationState.CONFIG, SimulationAction.START, SimulationState.RUNNING,
                (SimulationState fromState, SimulationAction action, SimulationState toState) -> this.startSimulation()
        );

        m_architectureEventController.addHandler(SimulationState.RUNNING, SimulationAction.PAUSE, SimulationState.PAUSED,
                (SimulationState fromState, SimulationAction action, SimulationState toState) -> this.pauseSimulation()
        );

        m_architectureEventController.addHandler(SimulationState.PAUSED, SimulationAction.RESUME, SimulationState.RUNNING,
                (SimulationState fromState, SimulationAction action, SimulationState toState) -> this.resumeSimulation()
        );


        m_architectureEventController.addHandler(SimulationState.CONFIG, SimulationAction.STOP, SimulationState.INIT,
                (SimulationState fromState, SimulationAction action, SimulationState toState) -> this.stopSimulation()
        );

        m_architectureEventController.addHandler(SimulationState.RUNNING, SimulationAction.STOP, SimulationState.INIT,
                (SimulationState fromState, SimulationAction action, SimulationState toState) -> this.stopSimulation()
        );

        m_architectureEventController.addHandler(SimulationState.PAUSED, SimulationAction.STOP, SimulationState.INIT,
                (SimulationState fromState, SimulationAction action, SimulationState toState) -> this.stopSimulation()
        );

        m_architectureEventController.addHandler(SimulationState.RUNNING, SimulationAction.GAMEOVER, SimulationState.DONE,
                (SimulationState fromState, SimulationAction action, SimulationState toState) -> this.stopSimulation()
        );

        simulationInstance = new TacticInstance(m_drone.getIdentifier());

        registerSubscriber();

        super.start();
    }

    public final void stopTactic() {
        this.stopThread();
        unconfigSimulation();
    }

    private void registerSubscriber() {
        try {
            this.m_subscriber.addTopic(MessageTopic.STATEUPDATES);
        } catch (IOException e) {
            log.fatal(e);
        }
        this.m_subscriber.addHandler(KillMessage.class, this);
    }

    @Override
    public final void destroy() {
    }

    private void configSimulation() {
        try {
            m_discoverer.register(simulationInstance);
            log.info("Registered tactic " + toString());
            registered = true;
        } catch (IOException | DuplicateName e) {
            log.fatal(e);
        }
    }

    private void unconfigSimulation() {
        if (registered) {
            try {
                m_discoverer.unregister(simulationInstance);
                log.info("Unregistered tactic " + toString());
                registered = false;
            } catch (IOException e) {
                log.fatal(e);
            }
        }
    }

    /**
     * Start the simulation.
     */
    private void startSimulation() {
        this.startThread();

        log.info("Started simulation!");

        if (!initialized.get()) {
            initializeTactics();
            initialized.set(true);
        }
    }

    /**
     * Pauses the simulation.
     */
    private void pauseSimulation() {
        this.pauseThread();

        log.info("Paused drone!");
    }

    /**
     * Resumes the simulation.
     */
    private void resumeSimulation() {
        this.resumeThread();

        log.info("Resumed drone!");
    }

    /**
     * Stops the simulation.
     */
    private void stopSimulation() {
        this.stopThread();
        unconfigSimulation();

        if (initialized.get()) {
            finalizeTactics();
            initialized.set(false);
        }
        log.info("Stopped drone!");
    }

    //-- MESSAGEHANDLERS

    /**
     * Handles a recieved message and calls the messagehandlers.
     *
     * @param message The received message.
     */
    public final void handleMessage(Message message) {
        if (message instanceof KillMessage) {
            handleKillMessage((KillMessage) message);
        }
    }

    /**
     * Handles a killMessage
     *
     * @param killMessage the received killMessage
     */
    private void handleKillMessage(KillMessage killMessage) {
        if (killMessage.getIdentifier().equals(m_drone.getIdentifier())) {
            log.info("Found kill message! Quitting for now...");
            this.stopSimulation();
        }
    }

    public final String getIdentifier() {
        return m_drone.getIdentifier();
    }

    //-- Helper methods for the implemented tactics
    protected final boolean hasComponents(String... components) {
        boolean result = true;
        for (String component : components) {
            switch (component) {
                case "radar":
                    result &= radar != null;
                    break;
                case "gps":
                    result &= gps != null;
                    break;
                case "engine":
                    result &= engine != null;
                    break;
                case "gun":
                    result &= gun != null;
                    break;
                case "radio":
                    result &= radio != null;
                    break;
            }
        }
        return result;
    }

    protected void validateRequiredComponents(String... requiredComponents) throws MissingComponentsException {
        if (!hasComponents(requiredComponents)) {
            throw new MissingComponentsException(requiredComponents);
        }
    }

    /**
     * -- Abstract metods
     */
    abstract void initializeTactics();

    /**
     * Method which is called to calculate and perform the new tactics. A tactic should implement this method with its
     * own logic.
     */
    abstract void calculateTactics();

    abstract void finalizeTactics();

    public class MissingComponentsException extends Exception {
        public MissingComponentsException(String... requiredComponents) {
            super("One of the following components is missing that was required: " + Arrays.toString
                    (requiredComponents));
        }
    }
}
