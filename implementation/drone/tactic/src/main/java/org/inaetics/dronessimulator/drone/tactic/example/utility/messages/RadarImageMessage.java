package org.inaetics.dronessimulator.drone.tactic.example.utility.messages;

import lombok.AccessLevel;
import lombok.Getter;
import org.inaetics.dronessimulator.common.protocol.TacticMessage;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.tactic.Tactic;

import java.util.HashMap;
import java.util.Map;

public class RadarImageMessage extends MyTacticMessage {
    @Getter(AccessLevel.PROTECTED)
    private Map<String, String> data = new HashMap<>();

    public RadarImageMessage(Tactic tactic, Map<String, D3Vector> radarImage) {
        super(tactic);
        radarImage.forEach((k, v) -> data.put(k, v.toString()));
    }

    public static Map<String, D3Vector> parseData(TacticMessage rawMessage) {
        Map<String, D3Vector> data = new HashMap<>();
        rawMessage.entrySet().stream().filter(e -> !e.getKey().equals("id") && !e.getKey().equals("type")).forEach(e -> data.put(e.getKey(), D3Vector.fromString(e.getValue())));
        return data;
    }
}
