package org.inaetics.dronessimulator.drone.tactic;

import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.common.vector.D3Vector;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

@Log4j
public class BasicTactic extends Tactic {
    private static final int HEARTBEAT = 1; // seconds
    private static final int TACTIC_UPDATE = 1; // seconds
    private static final int DRONE_TIMEOUT = 2; // seconds

    D3Vector moveTarget = new D3Vector(
            ThreadLocalRandom.current().nextDouble(20, 380),
            ThreadLocalRandom.current().nextDouble(20, 380),
            ThreadLocalRandom.current().nextDouble(20, 380));

    D3Vector attackTarget = null;

    Map<String, LocalDateTime> radarDrones = new HashMap<>();
    Map<String, LocalDateTime> gunDrones = new HashMap<>();

    boolean isRadar = false;
    String bossDrone = "";
    List<String> myGunDrones = new ArrayList<>();

    private LocalDateTime lastUpdateTactics = LocalDateTime.now();



    BasicTacticCommunication comm;

    @Override
    void initializeTactics() {
        log.info("Initializing tactics..");
        // determine whether i'm radarDrone or gunDrone
        if (radar != null) {
            isRadar = true;
        }

        // set up thread for communication
        comm = new BasicTacticCommunication(this, radio);
        Thread t = new Thread(comm);
        t.start();
    }


    @Override
    void calculateTactics() {
        if (LocalDateTime.now().isAfter(lastUpdateTactics.plusSeconds(1))) {
            updateTactics();
            lastUpdateTactics = LocalDateTime.now();
        }
        decideTactic();
        log.debug("isRadar: " + isRadar);
        log.debug("radardrones: " + radarDrones.size());
        log.debug("gunDrones: " + gunDrones.size());
        log.debug("myDrones: " + myGunDrones.size());
        calculateMovement();
    }

    private void updateTactics(){
        // update active drone lists
        radarDrones.entrySet().removeIf(entry -> entry.getValue().plusSeconds(DRONE_TIMEOUT).isBefore(LocalDateTime.now()));
        gunDrones.entrySet().removeIf(entry -> entry.getValue().plusSeconds(DRONE_TIMEOUT).isBefore(LocalDateTime.now()));

        // clear boss if he is no longer active
        if (!bossDrone.equals("") && !radarDrones.containsKey(bossDrone)) {
            bossDrone = "";
        }

        // get new boss if necessary
        if (!isRadar && bossDrone.equals("")) {
            comm.sendMessage(null, ProtocolTags.CONNECT_REQUEST, null);
        }

    }

    private void decideTactic() {


    }

    private void calculateMovement() {
        D3Vector position = gps.getPosition();
        if (position.distance_between(moveTarget) < 1) {
            if (gps.getVelocity().length() != 0) {
                engine.changeAcceleration(new D3Vector());
            }
            return;
        }

        D3Vector move;
        move = moveTarget.sub(position.add(gps.getVelocity()));
        engine.changeAcceleration(move);

    }

}
