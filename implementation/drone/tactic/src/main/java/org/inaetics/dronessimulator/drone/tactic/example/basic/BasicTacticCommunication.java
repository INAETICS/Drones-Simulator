package org.inaetics.dronessimulator.drone.tactic.example.basic;

import org.inaetics.dronessimulator.common.Tuple;
import org.inaetics.dronessimulator.common.protocol.TacticMessage;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.components.radio.Radio;
import org.inaetics.dronessimulator.pubsub.api.Message;

import java.time.LocalDateTime;

import static org.inaetics.dronessimulator.drone.tactic.example.basic.ProtocolTags.CONNECT_CONFIRM;

public class BasicTacticCommunication implements Runnable {

    private BasicTactic tactic;
    private Radio radio;

    private boolean go = true;

    public BasicTacticCommunication(BasicTactic tactic, Radio radio) {
        this.tactic = tactic;
        this.radio = radio;
    }

    //Logger
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(BasicTacticCommunication.class);

    @Override
    public void run() {

        while (go) {

            // get next message from queue and check if it is TacticMessage
            Object msg0 = radio.getMessages().poll();
            if (msg0 instanceof TacticMessage) {
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
                tactic.gunDrones.put(id, new Tuple<>(LocalDateTime.now(), D3Vector.fromString(msg.get("target"))));
                break;
            case HEARTBEAT_RADAR:
                tactic.radarDrones.put(id, new Tuple<>(LocalDateTime.now(), D3Vector.fromString(msg.get("target"))));
                break;
            case MOVE:
                tactic.moveTarget = D3Vector.fromString(msg.get("target"));
                break;
            case SHOOT:
                tactic.attackTarget = D3Vector.fromString(msg.get("target"));
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
