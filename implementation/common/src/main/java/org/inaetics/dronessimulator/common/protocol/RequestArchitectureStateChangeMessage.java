package org.inaetics.dronessimulator.common.protocol;


import lombok.Getter;
import lombok.Setter;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;

import java.util.Collections;
import java.util.List;

/**
 * A message to request a architecture state change
 */
@Getter
@Setter
public class RequestArchitectureStateChangeMessage extends ProtocolMessage {
    /**
     * The action to take
     */
    private SimulationAction action = null;

    @Override
    public List<MessageTopic> getTopics() {
        return Collections.singletonList(MessageTopic.ARCHITECTURE);
    }

    @Override
    public String toString() {
        return "RequestArchitectureStateChangeMessage " + action;
    }
}
