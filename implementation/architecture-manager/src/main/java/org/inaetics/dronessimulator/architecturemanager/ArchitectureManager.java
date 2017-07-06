package org.inaetics.dronessimulator.architecturemanager;

import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.RequestArchitectureStateChangeMessage;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.discovery.api.DuplicateName;
import org.inaetics.dronessimulator.discovery.api.Instance;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Group;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Architecture Manager service
 * Manages architecture wide concerns.
 * Currently this only consists of the current lifecycle state of the architecture
 * Uses Discovery and Subscriber to publish current state and receive requested state updates
 */
public class ArchitectureManager {
    /**
     * Reference to discovery bundle to publish state information
     */
    private volatile Discoverer m_discoverer;
    /**
     * Reference to subscriber to listen for state update requests
     */
    private volatile Subscriber m_subscriber;

    /**
     * The logger
     */
    private final static Logger logger = Logger.getLogger(ArchitectureManager.class);

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

        this.instance = new Instance(Type.SERVICE, Group.SERVICES, "architecture", getCurrentProperties());
    }

    /**
     * Construct a new Architecture Manager for testing purposes
     * @param discoverer The discoverer to use
     * @param subscriber The subscriber to use
     */
    public ArchitectureManager(Discoverer discoverer, Subscriber subscriber) {
        this();
        m_discoverer = discoverer;
        m_subscriber = subscriber;
    }

    /**
     * Start the Architecture Manager service
     * Registers the begin state in Discovery and
     * adds a handler to the subscriber to listen for
     * Architecture state change requests
     */
    public void start() {
        logger.info("Starting Architecture Manager...");
        try {
            // Register instance with discovery
            m_discoverer.register(this.instance);

            //Add subscriber handlers
            m_subscriber.addTopic(MessageTopic.ARCHITECTURE);
            m_subscriber.addHandler(RequestArchitectureStateChangeMessage.class, (Message _msg) -> {
                RequestArchitectureStateChangeMessage msg = (RequestArchitectureStateChangeMessage) _msg;
                SimulationAction action = msg.getAction();
                SimulationState nextState = nextState(this.currentState, action);

                if(nextState != null) {
                    // New state! Save and publish on discovery
                    this.previousState = this.currentState;
                    this.previousAction = action;
                    this.currentState = nextState;

                    logger.info("New transition: (" + this.previousState + ", " + this.previousAction + ", " + this.currentState + ")");

                    try {
                        instance = m_discoverer.updateProperties(instance, getCurrentProperties());
                    } catch (IOException e) {
                        logger.fatal(e);
                    }

                } else {
                    logger.error(String.format("Received an action which did not led to next state! Current state: %s. Action: %s", currentState, action));
                }
            });

        } catch(IOException | DuplicateName e) {
            logger.fatal(e);
        }

        logger.info("Started Architecture Manager!");
    }

    /**
     * Stops the Architecture Manager service
     * Unregisters the current state in Discovery
     */
    public void stop() {
        logger.info("Stopping Architecture Manager...");
        try {
            m_discoverer.unregister(instance);
        } catch (IOException e) {
            logger.error(e);
        }
        logger.info("Stopped Architecture Manager!");
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
