package org.inaetics.dronessimulator.architecturemanager;

import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;
import org.inaetics.dronessimulator.common.protocol.ArchitectureMessage;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
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

public class ArchitectureManager {
    private volatile Discoverer m_discoverer;
    private volatile Subscriber m_subscriber;

    private final static Logger logger = Logger.getLogger(ArchitectureManager.class);

    private Instance instance;

    private SimulationState previousState;
    private SimulationAction previousAction;
    private SimulationState currentState;

    public ArchitectureManager() {
        previousState = SimulationState.NOSTATE;
        previousAction = SimulationAction.INIT;
        currentState = SimulationState.INIT;

        this.instance = new Instance(Type.SERVICE, Group.SERVICES, "architecture", getCurrentProperties());
    }

    public void start() {
        logger.info("Starting Architecture Manager...");
        try {
            // Register instance with discovery
            m_discoverer.register(this.instance);

            //Add subscriber handlers
            m_subscriber.addTopic(MessageTopic.ARCHITECTURE);
            m_subscriber.addHandler(ArchitectureMessage.class, (Message _msg) -> {
                ArchitectureMessage msg = (ArchitectureMessage) _msg;
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

    public void stop() {
        logger.info("Stopping Architecture Manager...");
        try {
            m_discoverer.unregister(instance);
        } catch (IOException e) {
            logger.error(e);
        }
        logger.info("Stopped Architecture Manager!");
    }

    public Map<String, String> getCurrentProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("current_life_cycle", String.format("%s.%s.%s", previousState.toString(), previousAction.toString(), currentState.toString()));

        return properties;
    }

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
