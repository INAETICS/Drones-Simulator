package org.inaetics.dronessimulator.common.protocol;


import org.inaetics.dronessimulator.common.architecture.SimulationAction;

import java.util.Collections;
import java.util.List;

/**
 * A message to request a architecture state change
 */
public class RequestArchitectureStateChangeMessage extends ProtocolMessage {
    /**
     * The action to take
     */
    private SimulationAction action = null;

    @Override
    public List<MessageTopic> getTopics() {
        return Collections.singletonList(MessageTopic.ARCHITECTURE);
    }

    /**
     * Gets the requested action to take
     * @return The action
     */
    public SimulationAction getAction() {
        return action;
    }

    /**
     * Set the requested action in the message
     * @param action Which action to request
     */
    public void setAction(SimulationAction action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "RequestArchitectureStateChangeMessage " + action;
    }
}
