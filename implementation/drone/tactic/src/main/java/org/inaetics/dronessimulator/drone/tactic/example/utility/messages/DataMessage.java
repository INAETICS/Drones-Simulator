package org.inaetics.dronessimulator.drone.tactic.example.utility.messages;

import lombok.AccessLevel;
import lombok.Getter;
import org.inaetics.dronessimulator.drone.tactic.Tactic;

import java.util.HashMap;
import java.util.Map;

public class DataMessage extends MyTacticMessage {
    private final String messageType;
    @Getter(AccessLevel.PUBLIC)
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
