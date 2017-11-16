package org.inaetics.dronessimulator.drone.tactic.messages;

import lombok.AccessLevel;
import lombok.Getter;
import org.inaetics.dronessimulator.common.Tuple;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.tactic.Tactic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RadarImageMessage extends MyTacticMessage {
    @Getter(AccessLevel.PROTECTED)
    private Map<String, String> data = new HashMap<>();

    public RadarImageMessage(Tactic tactic, List<Tuple<String, D3Vector>> radarImage) {
        super(tactic);
        radarImage.forEach(tup -> data.put(tup.getLeft(), tup.getRight().toString()));
    }
}
