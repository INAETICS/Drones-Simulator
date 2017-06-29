package org.inaetics.dronessimulator.common.protocol;


import org.inaetics.dronessimulator.common.architecture.SimulationAction;

import java.util.Collections;
import java.util.List;

public class ArchitectureMessage extends ProtocolMessage {
    private SimulationAction action = null;

    @Override
    public List<MessageTopic> getTopics() {
        return Collections.singletonList(MessageTopic.ARCHITECTURE);
    }

    public SimulationAction getAction() {
        return action;
    }

    public void setAction(SimulationAction action) {
        this.action = action;
    }

    public String toString() {
        return "ArchitectureMessage " + action;
    }
}
