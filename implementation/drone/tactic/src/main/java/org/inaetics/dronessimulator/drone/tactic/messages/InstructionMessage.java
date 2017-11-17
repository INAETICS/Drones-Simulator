package org.inaetics.dronessimulator.drone.tactic.messages;

import lombok.AccessLevel;
import lombok.Getter;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.tactic.Tactic;

import java.util.HashMap;
import java.util.Map;

public class InstructionMessage extends MyTacticMessage {
    @Getter(AccessLevel.PROTECTED)
    private Map<String, String> data = new HashMap<>();

    public InstructionMessage(Tactic tactic, InstructionType type, D3Vector target) {
        super(tactic);
        data.put(InstructionType.class.getSimpleName(), type.name());
        data.put("target", String.valueOf(target));
    }

    public enum InstructionType {
        MOVE, SHOOT
    }
}
