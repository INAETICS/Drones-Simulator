package org.inaetics.dronessimulator.drone.tactic.example.utility.messages;

import lombok.AccessLevel;
import lombok.Getter;
import org.inaetics.dronessimulator.common.protocol.TacticMessage;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.tactic.Tactic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RadarImageMessage extends MyTacticMessage {
    @Getter(AccessLevel.PROTECTED)
    private Map<String, String> data = new HashMap<>();

    public RadarImageMessage(Tactic tactic, List<D3Vector> radarImage) {
        super(tactic);
        IntStream.range(0, radarImage.size()).parallel().forEach((i) -> data.put(String.valueOf(i), radarImage.get(i).toString()));
    }

    public static List<D3Vector> parseData(TacticMessage rawMessage) {
        return rawMessage.entrySet().parallelStream().filter(e -> !e.getKey().equals("id") && !e.getKey().equals("type")).map(e -> D3Vector.fromString(e
                .getValue())).collect(Collectors.toList());
    }
}
