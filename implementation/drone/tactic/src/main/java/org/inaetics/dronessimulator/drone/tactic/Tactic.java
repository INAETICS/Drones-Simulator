package org.inaetics.dronessimulator.drone.tactic;


import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.common.ManagedThread;
import org.inaetics.dronessimulator.common.Settings;
import org.inaetics.dronessimulator.common.TimeoutTimer;
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
import org.inaetics.pubsub.api.pubsub.Subscriber;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The abstract tactic each drone tactic should extend
 */
public abstract class Tactic extends ManagedThread implements Subscriber {
    private static final long TACTIC_TIMOUT = 1;//tck
    private final TimeoutTimer workTimoutTimer = new TimeoutTimer(TACTIC_TIMOUT * Settings.TICK_TIME);
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final TimeoutTimer ticker = new TimeoutTimer(Settings.TICK_TIME);

    /**
     * Create the logger
     */
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Tactic.class);

    // drone components
    protected volatile Radar radar;

    public Radar getRadar() {
        return radar;
    }

    protected volatile GPS gps;

    public GPS getGps() {
        return gps;
    }

    protected volatile Engine engine;

    public Engine getEngine() {
        return engine;
    }

    protected volatile Gun gun;

    public Gun getGun() {
        return gun;
    }

    protected volatile Radio radio;

    public Radio getRadio() {
        return radio;
    }

    /** The drone instance that can be used to get information about the current drone */
    protected volatile DroneInit drone;
    /**
     * Architecture Event controller bundle that is used to listen to state updates.
     */
    @SuppressWarnings("unused") //Assigned through OSGi
    private volatile ArchitectureEventController architectureEventController;
    /** The Subscriber to use for receiving messages */
    private Instance simulationInstance;
    private boolean registered = false;
    /**
     * Discoverer bundle
     */
    @SuppressWarnings("unused") //Assigned through OSGi
    private volatile Discoverer discoverer;

    /**
     * Thread implementation
     * <p>
     * Work calls the calulateTactics everytime the ticker is exceeded. The ticker runs on {@link Settings#TICK_TIME} ms.
     */
    @Override
    protected final void work() throws InterruptedException {
        if (ticker.timeIsExceeded()) {
            ticker.reset();
            //Start a timed thread that is interrupted after a specified timeout
            Thread t = new Thread(this::calculateTactics);
            t.start();
            workTimoutTimer.reset();
            while (t.isAlive()) {
                if (workTimoutTimer.timeIsExceeded()) {
                    t.interrupt();
                }
            }
        }
    }

    /**
     * Registers the handlers for the architectureEventController on startup. And registers the subscriber. Starts the tactic. This is called by Apache Felix.
     */
    public final void startTactic() {
        //@formatter:off
        architectureEventController.addHandler(SimulationState.INIT,    SimulationAction.CONFIG,   SimulationState.CONFIG,  (f,a,t) -> this.configSimulation());
        architectureEventController.addHandler(SimulationState.CONFIG,  SimulationAction.START,    SimulationState.RUNNING, (f,a,t) -> this.startSimulation());
        architectureEventController.addHandler(SimulationState.RUNNING, SimulationAction.PAUSE,    SimulationState.PAUSED,  (f,a,t) -> this.pauseSimulation());
        architectureEventController.addHandler(SimulationState.PAUSED,  SimulationAction.RESUME,   SimulationState.RUNNING, (f,a,t) -> this.resumeSimulation());
        architectureEventController.addHandler(SimulationState.CONFIG,  SimulationAction.STOP,     SimulationState.INIT,    (f,a,t) -> this.stopSimulation());
        architectureEventController.addHandler(SimulationState.RUNNING, SimulationAction.STOP,     SimulationState.INIT,    (f,a,t) -> this.stopSimulation());
        architectureEventController.addHandler(SimulationState.PAUSED,  SimulationAction.STOP,     SimulationState.INIT,    (f,a,t) -> this.stopSimulation());
        architectureEventController.addHandler(SimulationState.RUNNING, SimulationAction.GAMEOVER, SimulationState.DONE,    (f,a,t) -> this.stopSimulation());
        //@formatter:on

        simulationInstance = new TacticInstance(drone.getIdentifier());

        super.start();
    }

    /**
     * Stop the thread when Apache Felix calls this method.
     */
    public final void stopTactic() {
        this.stopThread();
        unconfigSimulation();
    }

    @Override
    @Deprecated
    public final void destroy() {
        //Do nothing on shutdown
    }

    private void configSimulation() {
        try {
            discoverer.register(simulationInstance);
            log.info("Registered tactic " + toString());
            registered = true;
        } catch (IOException | DuplicateName e) {
            log.fatal(e);
        }
    }

    private void unconfigSimulation() {
        if (registered) {
            try {
                discoverer.unregister(simulationInstance);
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

    /**
     * Handles a KillMessage by stopping the tactic and exiting the process.
     * Does nothing if msg is not of type KillMessage
     *
     * @param msg the received message
     */
    @Override
    public void receive(Object msg, MultipartCallbacks multipartCallbacks) {
        if (msg instanceof KillMessage && ((KillMessage) msg).getIdentifier().equals(drone.getIdentifier())) {
            log.info("Found kill message! Quitting for now... Last known movements: \n" +
                    "\tposition: " + gps.getPosition().toString() + "\n" +
                    "\tvelocity: " + gps.getVelocity().toString() + "\n" +
                    "\tacceleration: " + gps.getAcceleration().toString() + "\n"
            );
            this.stopSimulation();
            if (!log.isDebugEnabled()) {
                System.exit(10);
            }
        }
    }

    public final String getIdentifier() {
        return drone.getIdentifier();
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
                default:
                    log.warn(component + " is not a valid component to check for.");
            }
        }
        return result;
    }

    protected final void validateRequiredComponents(String... requiredComponents) throws MissingComponentsException {
        if (!hasComponents(requiredComponents)) {
            throw new MissingComponentsException(requiredComponents);
        }
    }

    /**
     * Checks if the components are non-null.
     *
     * @return returns a set with all available components.
     */
    public final Set<String> getAvailableComponents() {
        Set<String> componentlist = new HashSet<>();
        if (radar != null) {
            componentlist.add("radar");
        }
        if (gps != null) {
            componentlist.add("gps");
        }
        if (engine != null) {
            componentlist.add("engine");
        }
        if (radio != null) {
            componentlist.add("radio");
        }
        if (gun != null) {
            componentlist.add("gun");
        }
        return componentlist;
    }

    /**
     * Method which is called when the tactic is started.
     */
    protected abstract void initializeTactics();

    /**
     * Method which is called to calculate and perform the new tactics. A tactic should implement this method with its
     * own logic. Note that the code in this block must be executed within 1 tick see Settings.TICK_TIME and this
     * method is called repeatedly.
     */
    protected abstract void calculateTactics();


    /**
     * This method MIGHT be called when the drone is killed. This is not a guarantee. Use this to stop spawned threads to avoid infinite threads for each restart.
     */
    protected abstract void finalizeTactics();

    public class MissingComponentsException extends Exception {
        public MissingComponentsException(String... requiredComponents) {
            super("One of the following components is missing that was required: " + Arrays.toString(requiredComponents));
        }
    }
}
