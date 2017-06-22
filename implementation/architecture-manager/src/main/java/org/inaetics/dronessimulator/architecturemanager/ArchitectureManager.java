package org.inaetics.dronessimulator.architecturemanager;

import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.architecture.Action;
import org.inaetics.dronessimulator.common.architecture.State;
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

    private State previousState;
    private Action previousAction;
    private State currentState;

    public ArchitectureManager() {
        previousState = State.NOSTATE;
        previousAction = Action.INIT;
        currentState = State.CONFIG;

        this.instance = new Instance(Type.SERVICE, Group.SERVICES, "architecture", getCurrentProperties());
    }

    public void start() {
        try {
            // Register instance with discovery
            m_discoverer.register(this.instance);

            //Add subscriber handlers
            m_subscriber.addTopic(MessageTopic.ARCHITECTURE);
            m_subscriber.addHandler(ArchitectureMessage.class, (Message _msg) -> {
                ArchitectureMessage msg = (ArchitectureMessage) _msg;
                Action action = msg.getAction();
                State nextState = nextState(this.currentState, action);

                if(nextState != null) {
                    // New state! Save and publish on discovery
                    this.previousState = this.currentState;
                    this.previousAction = action;
                    this.currentState = nextState;

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
    }

    public void stop() {
        try {
            m_discoverer.unregister(instance);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    public Map<String, String> getCurrentProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("previous_state", previousState.toString());
        properties.put("previous_action", previousAction.toString());
        properties.put("current_state", currentState.toString());

        return properties;
    }

    public static State nextState(State currentState, Action action) {
        State nextState = null;

        switch(currentState) {
            case NOSTATE:
                switch(action) {
                    case INIT:
                        nextState = State.CONFIG;
                        break;
                }
                break;

            case CONFIG:
                switch(action) {
                    case START:
                        nextState = State.RUNNING;
                        break;
                    case STOP:
                        nextState = State.STOPPED;
                        break;
                }
                break;

            case RUNNING:
                switch(action) {
                    case STOP:
                        nextState = State.STOPPED;
                        break;
                    case PAUSE:
                        nextState = State.PAUSED;
                        break;
                    case GAMEOVER:
                        nextState = State.GAMEOVER;
                        break;
                }
                break;

            case STOPPED:
                switch(action) {
                    case RESTART:
                        nextState = State.CONFIG;
                        break;
                }
                break;

            case PAUSED:
                switch(action) {
                    case RESUME:
                        nextState = State.RUNNING;
                        break;
                }
                break;

            case GAMEOVER:
                switch(action) {
                    case RESTART:
                        nextState = State.CONFIG;
                        break;
                }
                break;
        }

        return nextState;
    }
}
