package org.inaetics.dronessimulator.drone.tactic.example.basic;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.inaetics.dronessimulator.drone.tactic.example.basic.ProtocolTags.HEARTBEAT_GUN;
import static org.inaetics.dronessimulator.drone.tactic.example.basic.ProtocolTags.HEARTBEAT_RADAR;

public class BasicTacticHeartbeat implements Runnable {

    private BasicTactic tactic;
    private BasicTacticCommunication comm;

    private boolean go = true;

    public BasicTacticHeartbeat(BasicTactic t, BasicTacticCommunication c) {
        tactic = t;
        comm = c;
    }

    @Override
    public void run() {
        LocalDateTime lastHeartBeat;
        while (go) {
            lastHeartBeat = LocalDateTime.now();

            // send a heartbeat every second
            if (LocalDateTime.now().isAfter(lastHeartBeat.plusSeconds(1))) {
                comm.sendMessage(null, (tactic.isRadar ? HEARTBEAT_RADAR : HEARTBEAT_GUN), null);
            }

            long diff = lastHeartBeat.until(LocalDateTime.now(), ChronoUnit.MILLIS);
            if (diff < 1000) {
                try {
                    Thread.sleep(diff);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        go = false;
    }
}
