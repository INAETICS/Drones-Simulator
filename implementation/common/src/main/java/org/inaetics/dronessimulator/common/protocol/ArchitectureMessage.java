package org.inaetics.dronessimulator.common.protocol;


import org.inaetics.dronessimulator.common.architecture.Action;

import java.util.Collections;
import java.util.List;

public class ArchitectureMessage extends ProtocolMessage {
    private Action action = null;


    @Override
    public List<MessageTopic> getTopics() {
        return Collections.singletonList(MessageTopic.ARCHITECTURE);
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
