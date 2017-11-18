package org.inaetics.dronessimulator.drone.tactic;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.common.Settings;
import org.inaetics.dronessimulator.common.Tuple;
import org.inaetics.dronessimulator.common.protocol.TacticMessage;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.tactic.messages.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
@NoArgsConstructor //An OSGi constructor
public class TheoreticalTactic extends Tactic {
    private static final long ttlLeader = 2; //seconds
    private DroneType droneType;
    private String idLeader;
    private HashMap<String, Tuple<LocalDateTime, List<String>>> teammembers = new HashMap<>();
    private Map<String, D3Vector> mapOfTheWorld = new HashMap<>();
    private Thread handleBroadcastMessagesThread;
    private D3Vector targetMoveLocation;
    private TimeoutTimer lastRequestForLeader = new TimeoutTimer(3000); //3 sec

    public final DroneType getType() {
        DroneType droneType;
        if (hasComponents("radar", "radio")) {
            droneType = DroneType.RADAR;
        } else if (hasComponents("gun", "radio")) {
            droneType = DroneType.GUN;
        } else {
            droneType = null;
        }
        return droneType;
    }

    @Override
    protected void initializeTactics() {
        droneType = getType();
        handleBroadcastMessagesThread = new Thread(this::handleReceivedTacticMessages);
        handleBroadcastMessagesThread.start();
        switch (droneType) {
            case GUN:
                //Send a message if you fire a bullet
                gun.registerCallback((fireBulletMessage) -> {
                    DataMessage shotMessage = new DataMessage(this, MyTacticMessage.MESSAGETYPES.FiredBulletMessage);
                    shotMessage.getData().put("direction", String.valueOf(fireBulletMessage.getDirection().orElse(null)));
                    shotMessage.getData().put("velocity", String.valueOf(fireBulletMessage.getVelocity().orElse(null)));
                    shotMessage.getData().put("firedMoment", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
                    radio.send(shotMessage.getMessage());
                });
                break;
            case RADAR:
                break;
        }
        log.debug("Tactic initialized for drone with type " + droneType);
    }

    /**
     * This calculates all the tactics.
     * <p>
     * The gun drones are simple. They just broadcast their location, at a specific interval, and direction when they
     * shoot at a target. They receive instructions from a radar drone and execute these. If they do not receive
     * instructions for a specified amount of time, they will start creating a kind of map of where friendly drones are,
     * and start shooting randomly, except for where friendly drones are.
     * <p>
     * The radar drones are more complex. If they see updates of other radar drones, they will communicate and combine
     * their vision power to get the best overview of the map. They will send the targets they have over to each other.
     * One will assume the leader role. The leader must include that in his heartbeat message and every drone will save
     * the current leader locally. If the leader does not send any heartbeat messages anymore, every drone will assume
     * it is destroyed and therefore possible new leaders will show up.
     */
    @Override
    protected void calculateTactics() {
        manageOutgoingCommunication();
//        moveToRandomLocation();
        //Check leader
        if (!checkIfLeaderIsAlive()) {
            log.debug("Find a new leader");
            //Leader is not alive
            findLeader();
            //Check if the new found leader is alive. This is done to avoid side-effects of a function
            if (lastRequestForLeader.timeIsExceeded()) { //If the last request is more
                // than 3 seconds ago and there still is no leader, just do random stuff
                //If it could not find a new (alive) leader, just start shooting if possible to any direction.
                if (DroneType.GUN.equals(droneType)) {
                    randomShooting();
                }
            }
        } else if (idLeader.equals(getIdentifier())) {
            sendInstructions();
        }
        if (targetMoveLocation != null) {
            moveToLocation(targetMoveLocation);
        }
    }

    @Override
    protected void finalizeTactics() {
        handleBroadcastMessagesThread.interrupt();
    }

    /**
     * This method handles all the required outgoing communication. All incoming communication should be done in
     * threads.
     */
    private void manageOutgoingCommunication() {
        broadcastHeartbeat();
        switch (droneType) {
            case GUN:
                break;
            case RADAR:
                sendRadarimage();
                break;
        }
    }

    private void handleReceivedTacticMessages() {
        TacticMessage newMessage = radio.getMessage(TacticMessage.class);
        log.debug("Has message? " + (newMessage != null));
        if (newMessage != null) {
            log.debug("Received a message with type " + String.valueOf(newMessage.get("type")));
            if (MyTacticMessage.checkType(newMessage, HeartbeatMessage.class)) {
                teammembers.put(newMessage.get("id"), new Tuple<>(LocalDateTime.now(),
                        Arrays.asList(newMessage.get("components").split(","))));
                mapOfTheWorld.put(newMessage.get("id"), D3Vector.fromString(newMessage.get("position")));
                switch (droneType) {
                    case GUN:
                        break;
                    case RADAR:

                        break;
                }
            } else if (MyTacticMessage.checkType(newMessage, InstructionMessage.class)) {
                executeInstruction(InstructionMessage.InstructionType.valueOf(newMessage.get(InstructionMessage.
                        InstructionType.class.getSimpleName())), D3Vector.fromString(newMessage.get("target")));
            } else if (MyTacticMessage.checkType(newMessage, MyTacticMessage.MESSAGETYPES.SearchLeaderMessage) &&
                    droneType.equals(DroneType.RADAR) && idLeader == null) {
                idLeader = getIdentifier();
                radio.send(new DataMessage(this, MyTacticMessage.MESSAGETYPES.IsLeaderMessage).getMessage());
                targetMoveLocation = new D3Vector(Math.random() * Settings.ARENA_WIDTH, Math.random() * Settings.ARENA_HEIGHT, Math.random() * Settings.ARENA_DEPTH);
                log.info("Drone " + getIdentifier() + " is the leader of team " + m_drone.getTeamname());
            } else if (MyTacticMessage.checkType(newMessage, MyTacticMessage.MESSAGETYPES.IsLeaderMessage)) {
                idLeader = newMessage.get("id");
                lastRequestForLeader.reset();
            }

        }
    }

    private boolean checkIfLeaderIsAlive() {
        return idLeader != null && teammembers.get(idLeader) != null && teammembers.get(idLeader).getLeft().isBefore
                (LocalDateTime.now().minusSeconds(ttlLeader));
    }

    private void executeInstruction(InstructionMessage.InstructionType instructionType, D3Vector targetLocation) {
        log.debug("Execute instruction " + instructionType + " with target " + String.valueOf(targetLocation));
        switch (instructionType) {
            case SHOOT:
                if (hasComponents("gun")) {
                    gun.fireBullet(targetLocation.toPoolCoordinate());
                } else {
                    log.error("Could not execute instruction " + instructionType + " with target " + String.valueOf
                            (targetLocation));
                }
                break;
            case MOVE:
                if (hasComponents("engine", "gps")) {
                    this.targetMoveLocation = targetLocation; //Store the target location to move there if there is no
                    // new instruction.
                    moveToLocation(targetLocation);
                } else {
                    log.error("Could not execute instruction " + instructionType + " with target " + String.valueOf
                            (targetLocation));
                }
                break;
        }
    }

    private void moveToLocation(D3Vector location) {
        D3Vector position = gps.getPosition();
        if (position.distance_between(location) < 1) {
            if (gps.getVelocity().length() != 0) {
                engine.changeAcceleration(engine.limit_acceleration(gps.getVelocity().scale(-1)));
            }
        } else {
            D3Vector move = location.sub(position.add(gps.getVelocity()));
            engine.changeAcceleration(move);
        }
    }

    private void findLeader() {
        if (idLeader == null) {
            log.debug("Searching for leader");
            radio.send(new DataMessage(this, MyTacticMessage.MESSAGETYPES.SearchLeaderMessage).getMessage());
            lastRequestForLeader.reset();
        }
    }

    private void randomShooting() {
        //TODO
        log.debug("RANDOM SHOOTING AND MOVING!!!");
        D3Vector randomLocation = new D3Vector(Math.random() * Settings.ARENA_WIDTH, Math.random() * Settings
                .ARENA_HEIGHT, Math.random() * Settings.ARENA_DEPTH);
        executeInstruction(InstructionMessage.InstructionType.SHOOT, randomLocation);
        executeInstruction(InstructionMessage.InstructionType.MOVE, randomLocation);
    }

    private void sendRadarimage() {
        List<Tuple<String, D3Vector>> currentRadar = radar.getRadar();
        RadarImageMessage radarImageMessage = new RadarImageMessage(this, currentRadar);
        log.debug("sendRadarimage: " + radarImageMessage.getMessage().toString());
        radio.send(radarImageMessage.getMessage());
    }

    private void broadcastHeartbeat() {
        log.debug("Send Heartbeat");
        HeartbeatMessage heartbeatMessage = new HeartbeatMessage(this, gps);
        if (getIdentifier().equals(idLeader)) { //If the drone is its own leader, he must be the leader of the team.
            heartbeatMessage.setIsLeader(true);
        }
        radio.send(heartbeatMessage.getMessage());
    }

    private void sendInstructions() {
        log.debug("sendInstructions type: MOVE, location: " + targetMoveLocation.toString());
        radio.send(new InstructionMessage(this, InstructionMessage.InstructionType.MOVE, targetMoveLocation).getMessage
                ());
    }

    private int calculateUtility() {
        return 0; //TODO
    }

    private enum DroneType {
        GUN, RADAR
    }


    @RequiredArgsConstructor
    private class TimeoutTimer {
        private final long timeout; //ms
        private long lastTime;

        public synchronized void reset() {
            lastTime = System.currentTimeMillis();
        }

        public synchronized boolean timeIsExceeded() {
            return (lastTime + timeout) < System.currentTimeMillis();
        }

    }
}
