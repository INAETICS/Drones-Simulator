package org.inaetics.dronessimulator.drone.tactic;

import java.time.LocalDateTime;

import static org.inaetics.dronessimulator.drone.tactic.ProtocolTags.HEARTBEAT_GUN;
import static org.inaetics.dronessimulator.drone.tactic.ProtocolTags.HEARTBEAT_RADAR;

public class BasicTacticHeartbeat implements Runnable{

    private BasicTactic tactic;
    private BasicTacticCommunication comm;

    private boolean go = true;

    public BasicTacticHeartbeat(BasicTactic t, BasicTacticCommunication c) {
        tactic = t;
        comm = c;
    }

    @Override
    public void run() {
        LocalDateTime lastHeartBeat = LocalDateTime.now();
        while (go) {
            // send a heartbeat every second
            if (LocalDateTime.now().isAfter(lastHeartBeat.plusSeconds(1))) {
                comm.sendMessage(null, (tactic.isRadar ? HEARTBEAT_RADAR : HEARTBEAT_GUN), null);
                lastHeartBeat = LocalDateTime.now();
            }
        }
    }

    public void stop() {
        go = false;
    }
}
