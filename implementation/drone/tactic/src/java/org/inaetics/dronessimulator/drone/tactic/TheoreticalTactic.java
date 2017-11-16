package org.inaetics.dronessimulator.drone.tactic;

import org.inaetics.dronessimulator.common.protocol.TacticMessage;

public class TheoreticalTactic extends Tactic {
    private final DroneType droneType;
    private String idLeader;

    public TheoreticalTactic() {
        if (hasComponents("radar", "radio")) {
            droneType = DroneType.RADAR;
        } else {
            droneType = DroneType.GUN;
        }
    }

    @Override
    void initializeTactics() {

    }

    @Override
    void calculateTactics() {
        manageCommunication();
    }

    /**
     * This method handles all the required communication, each step. The communication is the following:
     * <p>
     * The gun drones are simple. They just broadcast their location, at a specific interval, and direction when they shoot at a target. They receive instructions from a radar drone and execute these. If they do not receive instructions for a specified amount of time, they will start creating a kind of map of where friendly drones are, and start shooting randomly, except for where friendly drones are.
     */
    private void manageCommunication() {
        broadcastHeartbeat();
        switch (droneType) {
            case GUN:
                //if the last instruction is too old, then assume the leader is destroyed
                gun.registerCallback((fireBulletMessage) -> {
                    TacticMessage shotMessage = new TacticMessage();
                    shotMessage.put("id", getIdentifier());
                    shotMessage.put("direction", String.valueOf(fireBulletMessage.getDirection().orElse(null)));
                    radio.send(shotMessage);
                });
                break;
            case RADAR:
                break;
        }
    }

    private void broadcastHeartbeat() {
        TacticMessage heartbeatMessage = new TacticMessage();
        heartbeatMessage.put("id", getIdentifier());
        heartbeatMessage.put("position", gps.getPosition().toString());
        heartbeatMessage.put("direction", gps.getDirection().toString());
        heartbeatMessage.put("velocity", gps.getVelocity().toString());
        heartbeatMessage.put("acceleration", gps.getAcceleration().toString());
        radio.send(heartbeatMessage);
    }

    private int calculateUtility() {
        return 0;
    }

    private enum DroneType {
        GUN, RADAR
    }
}
