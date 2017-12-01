package org.inaetics.dronessimulator.drone.tactic;

import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.common.Settings;
import org.inaetics.dronessimulator.common.TimeoutTimer;
import org.inaetics.dronessimulator.common.vector.D3Vector;

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
    D3Vector attackTarget = null;
    Map<String, LocalDateTime> radarDrones = new HashMap<>();
    Map<String, LocalDateTime> gunDrones = new HashMap<>();
    boolean isRadar = false;
    String bossDrone = "";
    List<String> myGunDrones = new ArrayList<>();
    BasicTacticCommunication comm;
    BasicTacticHeartbeat heartbeat;
    Thread commThread;
    Thread heartbeatThread;
    private D3Vector lastPosition;
    private D3Vector lastAttackTarget;

    @Override
    protected void initializeTactics() {
        log.info("Initializing tactics..");
        // determine whether i'm radarDrone or gunDrone
        if (radar != null) {
            isRadar = true;
        }

//        // set up thread for communication
//        comm = new BasicTacticCommunication(this, radio);
//        commThread = new Thread(comm);
//        commThread.start();
//
//        heartbeat = new BasicTacticHeartbeat(this, comm);
//        heartbeatThread = new Thread(heartbeat);
//        heartbeatThread.start();
//
//        if (!isRadar) {
//            comm.sendMessage(null, ProtocolTags.CONNECT_REQUEST, null);
//        }
    }


    @Override
    protected void calculateTactics() {

        if (tacticTimer.timeIsExceeded()) {
            tacticTimer.reset();
            updateTactics();
        }
        if (moveTarget == null) {
            moveTarget = new D3Vector(ThreadLocalRandom.current().nextInt(100, 300), ThreadLocalRandom.current().nextInt(100, 300), ThreadLocalRandom.current().nextInt(100, 300));
        }

        calculateMovement();

        if (isRadar && (lastPosition == null || gps.getPosition().distance_between(lastPosition) > 1)) {
            organizeMovement();
            lastPosition = gps.getPosition();
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

    private void calculateMovement() {

        D3Vector position = gps.getPosition();
        log.debug("distance to target = " + position.distance_between(moveTarget));

        double distance = position.distance_between(moveTarget);
        double velocity = gps.getVelocity().length();

        // stationary, on target
        if (distance < 1 && velocity < 1) {
            log.debug("TEST21 - " + position.toString() + " - doing nothing.. ");
            engine.changeAcceleration(new D3Vector());
        }

        // stationary/not stationary, not on target, accelerating
        else if (distance > ((velocity * velocity) / (2 * Settings.MAX_DRONE_ACCELERATION))) {
            D3Vector newAcceleration = moveTarget.sub(position).scale(0.5);
            log.debug("TEST21 - " + position.toString() + " - accelerating.. " + newAcceleration);
            engine.changeAcceleration(newAcceleration);
        }

        // not stationary, not on target, decelerating
        else if (distance != 0) {

            double acceleration = -(velocity * velocity) / (2 * distance);
            D3Vector newAcceleration = (gps.getVelocity()).normalize().scale(acceleration);
            log.debug(String.format("CALCULOG d=%f, v=%f, a=%f", distance, velocity, acceleration));

            log.debug("TEST21 - " + position.toString() + " - decelerating.. " + newAcceleration);
            engine.changeAcceleration(newAcceleration);
        }
    }

    private void organizeMovement() {

        int number = myGunDrones.size();
        log.debug("number is " + number);
        double spawnRadius = 40;
        double spawnAngle = (2 * Math.PI) / number;

        int numberSpawned = 0;
        for (String id : myGunDrones) {
            D3Vector gunPosition = new D3Vector(Math.cos(spawnAngle * numberSpawned) * spawnRadius + gps.getPosition().getX()
                    , Math.sin(spawnAngle * numberSpawned) * spawnRadius + gps.getPosition().getY()
                    , gps.getPosition().getZ());
            numberSpawned++;
            comm.sendMessage(id, ProtocolTags.MOVE, gunPosition);
            log.debug("gundrone " + id + " target location set to " + gunPosition);
        }
    }

}
