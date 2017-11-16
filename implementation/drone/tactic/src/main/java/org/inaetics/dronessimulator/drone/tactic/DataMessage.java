package org.inaetics.dronessimulator.drone.tactic;

import lombok.AccessLevel;
import lombok.Getter;
import org.inaetics.dronessimulator.drone.tactic.messages.MyTacticMessage;

import java.util.HashMap;
import java.util.Map;

public class DataMessage extends MyTacticMessage {
    private final String messageType;
    @Getter(AccessLevel.PROTECTED)
    private Map<String, String> data = new HashMap<>();

    public DataMessage(Tactic tactic, String messageType) {
        super(tactic);
        this.messageType = messageType;
    }

    @Override
    protected String getType() {
        return messageType;
    }
}
