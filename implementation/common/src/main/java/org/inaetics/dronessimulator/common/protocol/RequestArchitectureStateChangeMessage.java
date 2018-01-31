package org.inaetics.dronessimulator.common.protocol;


import lombok.*;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;

import java.util.Collections;
import java.util.List;

/**
 * A message to request a architecture state change
 */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class RequestArchitectureStateChangeMessage extends ProtocolMessage {
    /**
     * The action to take
     */
    @Getter
    @Setter
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
