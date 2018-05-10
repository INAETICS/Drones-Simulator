package org.inaetics.dronessimulator.drone.tactic.example.utility.messages;

import org.inaetics.dronessimulator.drone.components.gps.GPS;
import org.inaetics.dronessimulator.drone.tactic.example.utility.TheoreticalTactic;

import java.util.HashMap;
import java.util.Map;

public class HeartbeatMessage extends MyTacticMessage {
    private final Map<String, String> data = new HashMap<>();

    @Override
    protected Map<String, String> getData() {
        return data;
    }

    public HeartbeatMessage(TheoreticalTactic tactic, GPS gps) {
        super(tactic);
        data.put("position", gps.getPosition().toString());
        data.put("direction", gps.getDirection().toString());
        data.put("velocity", gps.getVelocity().toString());
        data.put("acceleration", gps.getAcceleration().toString());
        data.put("components", String.join(",", tactic.getAvailableComponents().toArray(new String[]{})));
    }

    public void setIsLeader(boolean isLeader) {
        data.put("isLeader", String.valueOf(isLeader));
    }
}
