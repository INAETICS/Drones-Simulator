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
        currentState = SimulationState.CONFIG;

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
                        e.printStackTrace();
                        logger.fatal(e);
                    }

                } else {
                    logger.error(String.format("Received an action which did not led to next state! Current state: %s. Action: %s", currentState, action));
                }
            });

        } catch(IOException | DuplicateName e) {
            logger.fatal(e);
            e.printStackTrace();
        }

        logger.info("Started Architecture Manager!");
    }

    public void stop() {
        logger.info("Stopping Architecture Manager...");
        try {
            m_discoverer.unregister(instance);
        } catch (IOException e) {
            e.printStackTrace();
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
        SimulationState nextState = null;

        switch(currentState) {
            case NOSTATE:
                switch(action) {
                    case INIT:
                        nextState = SimulationState.CONFIG;
                        break;
                }
                break;

            case CONFIG:
                switch(action) {
                    case START:
                        nextState = SimulationState.RUNNING;
                        break;
                    case STOP:
                        nextState = SimulationState.STOPPED;
                        break;
                }
                break;

            case RUNNING:
                switch(action) {
                    case STOP:
                        nextState = SimulationState.STOPPED;
                        break;
                    case PAUSE:
                        nextState = SimulationState.PAUSED;
                        break;
                    case GAMEOVER:
                        nextState = SimulationState.GAMEOVER;
                        break;
                }
                break;

            case STOPPED:
                switch(action) {
                    case RESTART:
                        nextState = SimulationState.CONFIG;
                        break;
                }
                break;

            case PAUSED:
                switch(action) {
                    case RESUME:
                        nextState = SimulationState.RUNNING;
                        break;
                }
                break;

            case GAMEOVER:
                switch(action) {
                    case RESTART:
                        nextState = SimulationState.CONFIG;
                        break;
                }
                break;
        }

        return nextState;
    }
}
