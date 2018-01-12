package org.inaetics.dronessimulator.drone.tactic.example.basic;

import lombok.extern.log4j.Log4j;

import java.time.LocalDateTime;

import static org.inaetics.dronessimulator.drone.tactic.example.basic.ProtocolTags.HEARTBEAT_GUN;
import static org.inaetics.dronessimulator.drone.tactic.example.basic.ProtocolTags.HEARTBEAT_RADAR;

@Log4j
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
                lastHeartBeat = LocalDateTime.now();
                comm.sendMessage(null, (tactic.isRadar ? HEARTBEAT_RADAR : HEARTBEAT_GUN), null);
                log.debug("sending heartbeat");
            }


        }
    }

    public void stop() {
        go = false;
    }
}