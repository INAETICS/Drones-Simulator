package org.inaetics.dronessimulator.drone.tactic.example.utility.messages;

import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.tactic.Tactic;

import java.util.HashMap;
import java.util.Map;

public class InstructionMessage extends MyTacticMessage {
    private final Map<String, String> data = new HashMap<>();

    @Override
    protected Map<String, String> getData() {
        return data;
    }

    public InstructionMessage(Tactic tactic, InstructionType type, String instructionIsFor, D3Vector target) {
        super(tactic);
        data.put(InstructionType.class.getSimpleName(), type.name());
        data.put("receiver", instructionIsFor);
        data.put("target", String.valueOf(target));
    }

    public enum InstructionType {
        MOVE, SHOOT
    }
}
