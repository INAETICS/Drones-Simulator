package org.inaetics.dronessimulator.drone.tactic.example.utility.messages;

import org.inaetics.dronessimulator.drone.tactic.Tactic;

import java.util.HashMap;
import java.util.Map;

public class DataMessage extends MyTacticMessage {
    private final String messageType;

    private Map<String, String> data = new HashMap<>();

    @Override
    public Map<String, String> getData() {
        return data;
    }

    public DataMessage(Tactic tactic, String messageType) {
        super(tactic);
        this.messageType = messageType;
    }

    @Override
    protected String getType() {
        return messageType;
    }
}
