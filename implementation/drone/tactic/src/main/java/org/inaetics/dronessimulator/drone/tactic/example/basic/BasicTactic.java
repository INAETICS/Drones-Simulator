package org.inaetics.dronessimulator.drone.tactic.example.basic;

import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.common.TimeoutTimer;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.tactic.Tactic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Log4j
public class BasicTactic extends Tactic {
    private static final int DRONE_TIMEOUT = 2; // seconds
    private final TimeoutTimer tacticTimer = new TimeoutTimer(1000); //ms

    D3Vector moveTarget = null;
    D3Vector lastMoveTarget = null;
    D3Vector attackTarget = null;

    Map<String, LocalDateTime> radarDrones = new HashMap<>();
    Map<String, LocalDateTime> gunDrones = new HashMap<>();

    boolean isRadar = false;

    String bossDrone = "";

    List<String> myGunDrones = new ArrayList<>();
    boolean myGunDronesChanged;

    BasicTacticCommunication comm;
    BasicTacticHeartbeat heartbeat;
    Thread commThread;
    Thread heartbeatThread;

    @Override
    protected void initializeTactics() {
        log.info("Initializing tactics..");
        // determine whether i'm radarDrone or gunDrone
        if (radar != null) {
            isRadar = true;
        }

        // set up thread for communication
        comm = new BasicTacticCommunication(this, radio);
        commThread = new Thread(comm);
        commThread.start();

        heartbeat = new BasicTacticHeartbeat(this, comm);
        heartbeatThread = new Thread(heartbeat);
        heartbeatThread.start();

        if (!isRadar) {
            comm.sendMessage(null, ProtocolTags.CONNECT_REQUEST, null);
        }
    }

    @Override
    protected void calculateTactics() {

        if (tacticTimer.timeIsExceeded()) {
            tacticTimer.reset();
            updateTactics();
        }

        if (isRadar) {
            if (moveTarget == null) {
                moveTarget = new D3Vector(ThreadLocalRandom.current().nextInt(100, 300), ThreadLocalRandom.current().nextInt(100, 300), ThreadLocalRandom.current().nextInt(100, 300));
                log.debug("Initialized move target to: " + moveTarget);
            }

            if (lastMoveTarget == null || !lastMoveTarget.equals(moveTarget) || myGunDronesChanged) {
                organizeMovement();
                myGunDronesChanged = false;
                lastMoveTarget = moveTarget;
            }
        }

        calculateMovements();
    }

    protected void calculateMovements() {
        if (moveTarget != null) {
            engine.changeVelocity(moveTarget.sub(gps.getPosition()));
        }
    }

    @Override
    protected void finalizeTactics() {
//        comm.stop();
    }

    private void updateTactics() {
        // update active drone lists
        radarDrones.entrySet().removeIf(entry -> entry.getValue().plusSeconds(DRONE_TIMEOUT).isBefore(LocalDateTime.now()));
        gunDrones.entrySet().removeIf(entry -> entry.getValue().plusSeconds(DRONE_TIMEOUT).isBefore(LocalDateTime.now()));

        // clear boss if he is no longer active
        if (!bossDrone.equals("") && !radarDrones.containsKey(bossDrone)) {
            bossDrone = "";
        }

        // get new boss if necessary
        if (comm != null && !isRadar && bossDrone.equals("")) {
            comm.sendMessage(null, ProtocolTags.CONNECT_REQUEST, null);
        }

    }

    private void organize() {
//        radar.getNearestTarget().ifPresent(x -> moveTarget = x.getRight());
//        for (String id : gunDrones.keySet()) {
//            comm.sendMessage(id, ProtocolTags.SHOOT, attackTarget);
//        }

    }

    private void organizeMovement() {

        D3Vector moveFocus = gps.getPosition();
        if (moveTarget != null) {
            moveFocus = moveTarget;
        }

        int number = myGunDrones.size();
        log.debug("number is " + number);
        double spawnRadius = 40;
        double spawnAngle = (2 * Math.PI) / number;

        int numberSpawned = 0;
        for (String id : myGunDrones) {
            D3Vector gunPosition = new D3Vector(Math.cos(spawnAngle * numberSpawned) * spawnRadius + moveFocus.getX()
                    , Math.sin(spawnAngle * numberSpawned) * spawnRadius + moveFocus.getY()
                    , moveFocus.getZ());
            numberSpawned++;
            comm.sendMessage(id, ProtocolTags.MOVE, gunPosition);
            log.debug("gundrone " + id + " target location set to " + gunPosition);
        }
    }

    public void addGunDrone(String id) {
        myGunDrones.add(id);
        myGunDronesChanged = true;
    }

}