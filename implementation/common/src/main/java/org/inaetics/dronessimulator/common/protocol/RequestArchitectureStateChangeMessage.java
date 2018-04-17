package org.inaetics.dronessimulator.common.protocol;


import org.inaetics.dronessimulator.common.architecture.SimulationAction;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A message to request a architecture state change
 */

public class RequestArchitectureStateChangeMessage extends ProtocolMessage {

    /**
     * The action to take
     */
    private SimulationAction action = null;

    public RequestArchitectureStateChangeMessage() {

    }

    public RequestArchitectureStateChangeMessage(SimulationAction action) {
        this.action = action;
    }

    public SimulationAction getAction() {
        return action;
    }

    public void setAction(SimulationAction action) {
        this.action = action;
    }



    @Override
    public List<MessageTopic> getTopics() {
        return Collections.singletonList(MessageTopic.ARCHITECTURE);
    }

    @Override
    public String toString() {
        return "RequestArchitectureStateChangeMessage " + action;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RequestArchitectureStateChangeMessage)) return false;
        RequestArchitectureStateChangeMessage that = (RequestArchitectureStateChangeMessage) o;
        return action == that.action;
    }

    @Override
    public int hashCode() {

        return Objects.hash(action);
    }
}
