package org.inaetics.dronessimulator.drone.tactic.example.basic;

import org.inaetics.dronessimulator.common.Settings;
import org.inaetics.dronessimulator.common.TimeoutTimer;
import org.inaetics.dronessimulator.common.Tuple;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.components.radar.Radar;
import org.inaetics.dronessimulator.drone.tactic.Tactic;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class BasicTactic extends Tactic {
    private static final int DRONE_TIMEOUT = 2; // seconds
    private final TimeoutTimer tacticTimer = new TimeoutTimer(1000); //ms

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(BasicTactic.class);

    D3Vector moveTarget = null;
    D3Vector lastMoveTarget = null;
    D3Vector attackTarget = null;

    Map<String, Tuple<LocalDateTime, D3Vector>> radarDrones = new HashMap<>();
    Map<String, Tuple<LocalDateTime, D3Vector>> gunDrones = new HashMap<>();

    boolean isRadar = false;

    String bossDrone = "";

    List<String> myGunDrones = new ArrayList<>();
    boolean myGunDronesChanged;

    BasicTacticCommunication comm;
    BasicTacticHeartbeat heartbeat;
    Thread commThread;
    Thread heartbeatThread;

    int randomXmin = (int) Math.min(Radar.RADAR_RANGE, Settings.ARENA_WIDTH / 2);
    int randomYmin = (int) Math.min(Radar.RADAR_RANGE, Settings.ARENA_DEPTH / 2);
    int randomZmin = (int) Math.min(Radar.RADAR_RANGE, Settings.ARENA_HEIGHT / 2);
    int randomXmax = (int) Math.max(Settings.ARENA_WIDTH - Radar.RADAR_RANGE, Settings.ARENA_WIDTH / 2 + 1);
    int randomYmax = (int) Math.max(Settings.ARENA_DEPTH - Radar.RADAR_RANGE, Settings.ARENA_DEPTH / 2 + 1);
    int randomZmax = (int) Math.max(Settings.ARENA_HEIGHT - Radar.RADAR_RANGE, Settings.ARENA_HEIGHT / 2 + 1);


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
            if (moveTarget == null || getEnemy().isPresent()) {
                moveTarget = getEnemy().isPresent() ? getDistanceTarget(getEnemy().get(), 80) : new D3Vector(ThreadLocalRandom.current().nextInt(randomXmin, randomXmax), ThreadLocalRandom.current().nextInt(randomYmin, randomYmax), ThreadLocalRandom.current().nextInt(randomZmin, randomZmax));
            }

            if (lastMoveTarget == null || !lastMoveTarget.equals(moveTarget) || myGunDronesChanged) {
                organizeMovement();
                myGunDronesChanged = false;
                lastMoveTarget = moveTarget;
            }

            organizeShooting();
        } else {
            shoot();
        }

        calculateMovements();
    }

    /**
     * Creates a new position based on the move target and the distance the drone should stay away from it.
     * This position is in the exact direction the drone currently is from the target.
     * @param target D3Vector move target
     * @param distance int distance that should be kept from the move target
     * @return D3Vector a position, distance from the target
     */
    private D3Vector getDistanceTarget(D3Vector target, int distance) {
        D3Vector curDistance = target.sub(gps.getPosition());
        D3Vector direction = curDistance.normalize();
        D3Vector distanceVector = direction.scale(distance);
        D3Vector newTarget = target.sub(distanceVector);

        return newTarget;
    }

    private void calculateMovements() {
        if (moveTarget != null) {
            if (moveTarget.sub(gps.getPosition()).length() < 2) {
                moveTarget = null;
                engine.changeVelocity(new D3Vector());
            } else {
                engine.changeVelocity(moveTarget.sub(gps.getPosition()));
            }
        }
    }

    @Override
    protected void finalizeTactics() {
        comm.stop();
    }

    private void updateTactics() {
        // update active drone lists
        radarDrones.entrySet().removeIf(entry -> entry.getValue().getLeft().plusSeconds(DRONE_TIMEOUT).isBefore(LocalDateTime.now()));
        gunDrones.entrySet().removeIf(entry -> entry.getValue().getLeft().plusSeconds(DRONE_TIMEOUT).isBefore(LocalDateTime.now()));

        // clear boss if he is no longer active
        if (!bossDrone.equals("") && !radarDrones.containsKey(bossDrone)) {
            bossDrone = "";
        }

        // get new boss if necessary
        if (comm != null && !isRadar && bossDrone.equals("")) {
            comm.sendMessage(null, ProtocolTags.CONNECT_REQUEST, null);
        }

    }

    private void organizeShooting() {
        getEnemy().ifPresent(x -> attackTarget = x);
        for (String id : myGunDrones) {
            comm.sendMessage(id, ProtocolTags.SHOOT, attackTarget);
        }

    }

    private void shoot() {
        if (attackTarget != null) {
            gun.fireBullet(attackTarget.sub(gps.getPosition()).toPoolCoordinate());
            attackTarget = null;
        }
    }

    private void organizeMovement() {

        D3Vector moveFocus = gps.getPosition();
        if (moveTarget != null) {
            moveFocus = moveTarget;
        }

        int number = myGunDrones.size();
        double spawnRadius = 40;
        double spawnAngle = (2 * Math.PI) / number;

        int numberSpawned = 0;
        for (String id : myGunDrones) {
            D3Vector gunPosition = new D3Vector(Math.cos(spawnAngle * numberSpawned) * spawnRadius + moveFocus.getX()
                    , Math.sin(spawnAngle * numberSpawned) * spawnRadius + moveFocus.getY()
                    , moveFocus.getZ());
            numberSpawned++;
            comm.sendMessage(id, ProtocolTags.MOVE, gunPosition);
        }
    }

    public void addGunDrone(String id) {
        myGunDrones.add(id);
        myGunDronesChanged = true;
    }

    private Optional<D3Vector> getEnemy() {
        Map<String, Tuple<LocalDateTime, D3Vector>> teammembers = new HashMap<>();
        teammembers.putAll(radarDrones);
        teammembers.putAll(gunDrones);
        return radar.getRadar().parallelStream()
                .filter(ral -> //Get positions of the teammembers
                        teammembers.entrySet().parallelStream().map(tm -> tm.getValue().getRight()).noneMatch(t -> t.distance_between(ral) < Settings.MAX_DRONE_VELOCITY)).min(Comparator.comparingDouble(e -> e.distance_between(gps.getPosition())));
    }

}