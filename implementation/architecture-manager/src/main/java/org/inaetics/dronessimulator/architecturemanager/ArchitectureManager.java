package org.inaetics.dronessimulator.architecturemanager;

import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;
import org.inaetics.dronessimulator.common.protocol.RequestArchitectureStateChangeMessage;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.discovery.api.DuplicateName;
import org.inaetics.dronessimulator.discovery.api.Instance;
import org.inaetics.dronessimulator.discovery.api.instances.ArchitectureInstance;
import org.inaetics.pubsub.api.pubsub.Subscriber;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Architecture Manager service
 * Manages architecture wide concerns.
 * Currently this only consists of the current lifecycle state of the architecture
 * Uses Discovery and Subscriber to publish current state and receive requested state updates
 */

public class ArchitectureManager implements Subscriber {
    /**
     * Reference to discovery bundle to publish state information
     */
    private volatile Discoverer discoverer;

    /**
     * The instance published in Discovery
     */
    private Instance instance;

    /**
     * The current previous state of the architecture
     */
    private SimulationState previousState;

    /**
     * The last action taken by the architecture
     */
    private SimulationAction previousAction;

    /**
     * The current state of the architecture
     */
    private SimulationState currentState;

    /**
     * Construct a new Architecture Manager
     * Sets the begin state of the architecture and created the instance for Discovery
     */
    public ArchitectureManager() {
        previousState = SimulationState.NOSTATE;
        previousAction = SimulationAction.INIT;
        currentState = SimulationState.INIT;

        this.instance = new ArchitectureInstance(getCurrentProperties());
    }

    /**
     *Create the logger
     */
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ArchitectureManager.class);


    @Override
    public void receive(Object o, MultipartCallbacks multipartCallbacks) {
        System.out.println("[ArchitectureManager] Got message " + o);
        if (o instanceof RequestArchitectureStateChangeMessage) {
            RequestArchitectureStateChangeMessage msg = (RequestArchitectureStateChangeMessage) o;
            SimulationAction action = msg.getAction();
            SimulationState nextState = nextState(this.currentState, action);

            if (nextState != null) {
                // New state! Save and publish on discovery
                this.previousState = this.currentState;
                this.previousAction = action;
                this.currentState = nextState;

                log.info("New transition: (" + this.previousState + ", " + this.previousAction + ", " + this.currentState + ")");

                instance = safeUpdateProperties(instance, getCurrentProperties());

            } else {
                log.error(String.format("Received an action which did not led to next state! Current state: %s. Action: %s", currentState, action));
            }
        }
    }

    /**
     * Start the Architecture Manager service, with the Subscriber being initialized by OSGi
     */
    public void start() {
        log.info("\n\nStarted Architecture Manager!\n\n");
        try {
            discoverer.register(instance);
        } catch (DuplicateName duplicateName) {
            duplicateName.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Instance safeUpdateProperties(final Instance instance, final Map<String, String> properties) {
        try {
            return discoverer.updateProperties(instance, properties);
        } catch (IOException e) {
            log.fatal(e);
        }
        return instance;
    }

    /**
     * Stops the Architecture Manager service
     * Unregisters the current state in Discovery
     */
    public void stop() {
        log.info("Stopping Architecture Manager...");
        try {
            discoverer.unregister(instance);
        } catch (IOException e) {
            log.error(e);
        }
        log.info("Stopped Architecture Manager!");
    }

    /**
     * Get the current lifecycle state in a map
     * which can be used by Discovery Instance
     * @return The map to be published with the Instance
     */
    public Map<String, String> getCurrentProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("current_life_cycle", String.format("%s.%s.%s", previousState.toString(), previousAction.toString(), currentState.toString()));

        return properties;
    }

    /**
     * The next state of the architecture based on the current state and the taken action
     * @param currentState The current state of the architecture
     * @param action The action to take for the architecture
     * @return The new state after the action is taken
     */
    public static SimulationState nextState(SimulationState currentState, SimulationAction action) {
        SimulationState nextState;

        switch(currentState) {
            case NOSTATE:
                nextState = nextStateFromNoState(action);
                break;

            case INIT:
                nextState = nextStateFromInit(action);
                break;

            case CONFIG:
                nextState = nextStateFromConfig(action);
                break;

            case RUNNING:
                nextState = nextStateFromRunning(action);
                break;

            case PAUSED:
                nextState = nextStateFromPaused(action);
                break;

            case DONE:
                nextState = nextStateFromDone(action);
                break;

            default:
                nextState = null;
                break;
        }

        return nextState;
    }

    private static SimulationState nextStateFromNoState(SimulationAction action) {
        SimulationState nextState;

        switch(action) {
            case INIT:
                nextState = SimulationState.INIT;
                break;
            default:
                nextState = null;
                break;
        }

        return nextState;
    }

    private static SimulationState nextStateFromInit(SimulationAction action) {
        SimulationState nextState;

        switch(action) {
            case CONFIG:
                nextState = SimulationState.CONFIG;
                break;
            default:
                nextState = null;
                break;
        }

        return nextState;
    }

    private static SimulationState nextStateFromConfig(SimulationAction action) {
        SimulationState nextState;

        switch(action) {
            case START:
                nextState = SimulationState.RUNNING;
                break;
            case STOP:
                nextState = SimulationState.INIT;
                break;
            default:
                nextState = null;
                break;
        }

        return nextState;
    }

    private static SimulationState nextStateFromRunning(SimulationAction action) {
        SimulationState nextState;

        switch(action) {
            case STOP:
                nextState = SimulationState.INIT;
                break;
            case PAUSE:
                nextState = SimulationState.PAUSED;
                break;
            case GAMEOVER:
                nextState = SimulationState.DONE;
                break;
            default:
                nextState = null;
                break;
        }

        return nextState;
    }

    private static SimulationState nextStateFromPaused(SimulationAction action) {
        SimulationState nextState;

        switch(action) {
            case RESUME:
                nextState = SimulationState.RUNNING;
                break;
            case STOP:
                nextState = SimulationState.INIT;
                break;
            default:
                nextState = null;
                break;
        }

        return nextState;
    }

    private static SimulationState nextStateFromDone(SimulationAction action) {
        SimulationState nextState;

        switch(action) {
            case STOP:
                nextState = SimulationState.INIT;
                break;
            default:
                nextState = null;
                break;
        }

        return nextState;
    }
}
