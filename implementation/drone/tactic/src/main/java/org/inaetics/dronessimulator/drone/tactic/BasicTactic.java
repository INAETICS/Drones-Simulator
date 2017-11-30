package org.inaetics.dronessimulator.drone.tactic;

import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.common.Settings;
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

    D3Vector moveTarget = new D3Vector(ThreadLocalRandom.current().nextInt(100, 300), ThreadLocalRandom.current().nextInt(100, 300), ThreadLocalRandom.current().nextInt(100, 300));
    D3Vector attackTarget = null;

    private D3Vector lastPosition;
    private D3Vector lastAttackTarget;

    Map<String, LocalDateTime> radarDrones = new HashMap<>();
    Map<String, LocalDateTime> gunDrones = new HashMap<>();

    boolean isRadar = false;
    String bossDrone = "";
    List<String> myGunDrones = new ArrayList<>();
    BasicTacticCommunication comm;
    BasicTacticHeartbeat heartbeat;
    Thread commThread;
    Thread heartbeatThread;
    private LocalDateTime lastUpdateTactics = LocalDateTime.now().minusSeconds(10);

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

        if (LocalDateTime.now().isAfter(lastUpdateTactics.plusSeconds(1))) {
            lastUpdateTactics = LocalDateTime.now();
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

        if (position.distance_between(moveTarget) < 1) {
            if (gps.getVelocity().length() != 0) {
                engine.changeAcceleration(gps.getVelocity().scale(-1));
            }
        } else {
            log.debug("Velocity is: " + gps.getVelocity().length());
            if (velocity == 0 || position.distance_between(moveTarget) > ((velocity * velocity) / (2 * Settings.MAX_DRONE_ACCELERATION))) {
                log.debug("accelerating..");
                engine.changeAcceleration(moveTarget.sub(position.add(gps.getVelocity())));
            } else {
                log.debug("decelerating..");
                double acceleration = -(velocity * velocity) / (2 * distance);
                D3Vector newAcceleration = (moveTarget.sub(position)).normalize().scale(acceleration);
                log.debug(String.format("CALCULOG d=%f, v=%f, a=%f", distance, velocity, acceleration));

                engine.changeAcceleration(newAcceleration);
            }
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
