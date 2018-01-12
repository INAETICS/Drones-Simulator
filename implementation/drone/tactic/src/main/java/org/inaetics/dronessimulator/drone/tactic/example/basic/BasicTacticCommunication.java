package org.inaetics.dronessimulator.drone.tactic.example.basic;

import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.common.protocol.TacticMessage;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.components.radio.Radio;
import org.inaetics.dronessimulator.pubsub.api.Message;

import java.time.LocalDateTime;

import static org.inaetics.dronessimulator.drone.tactic.example.basic.ProtocolTags.CONNECT_CONFIRM;

@Log4j
public class BasicTacticCommunication implements Runnable {

    private BasicTactic tactic;
    private Radio radio;

    private boolean go = true;

    public BasicTacticCommunication(BasicTactic tactic, Radio radio) {
        this.tactic = tactic;
        this.radio = radio;
    }

    @Override
    public void run() {

        while (go) {

            // get next message from queue and check if it is TacticMessage
            Message msg0 = radio.getMessages().poll();
            if (msg0 != null || msg0 instanceof TacticMessage) {
                // cast it
                TacticMessage msg = (TacticMessage) msg0;

                handleMessage(msg);
            }

        }
    }

    private void handleMessage(TacticMessage msg) {
        String id = msg.get("id");
        if (msg.get("id").equals(tactic.getIdentifier()) || !(msg.get("receiver").equals(tactic.getIdentifier()) || msg.get("receiver").equals("all"))) {
            return;
        }
        switch (ProtocolTags.valueOf(msg.get("tag"))) {
            case HEARTBEAT_GUN:
                tactic.gunDrones.put(id, LocalDateTime.now());
                break;
            case HEARTBEAT_RADAR:
                tactic.radarDrones.put(id, LocalDateTime.now());
                break;
            case MOVE:
                tactic.moveTarget = D3Vector.fromString(msg.get("target"));
                log.info("Moving to " + tactic.moveTarget.toString());
                break;
            case SHOOT:
                tactic.attackTarget = D3Vector.fromString(msg.get("target"));
                log.info("Attacking " + tactic.attackTarget);
                break;
            case CONNECT_REQUEST:
                if (tactic.isRadar && (tactic.myGunDrones.size() < 2 || tactic.radarDrones.size() < 2)) {
                    sendMessage(id, CONNECT_CONFIRM, null);
                }
                break;
            case CONNECT_CONFIRM:
                if (tactic.isRadar) {
                    if (!tactic.myGunDrones.contains(id)) {
                        tactic.addGunDrone(id);
                        log.info("gun drone " + id + " added");
                    }
                } else if (tactic.bossDrone.equals("")) {
                    sendMessage(id, CONNECT_CONFIRM, null);
                    tactic.bossDrone = id;
                    log.info("boss drone set to " + id);
                }
                break;
            default:
                log.warn("message not known: " + msg.toString());
        }
    }

    protected void sendMessage(String receiver, ProtocolTags tag, D3Vector target) {
        if (receiver == null) receiver = "all";
        TacticMessage msg = new TacticMessage();
        msg.put("id", tactic.getIdentifier());
        msg.put("receiver", receiver);
        msg.put("tag", String.valueOf(tag));
        msg.put("target", String.valueOf(target));
        radio.send(msg);
    }

    protected void stop() {
        go = false;
    }
}
