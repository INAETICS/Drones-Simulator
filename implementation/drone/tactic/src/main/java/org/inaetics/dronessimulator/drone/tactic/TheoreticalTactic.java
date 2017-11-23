package org.inaetics.dronessimulator.drone.tactic;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.common.*;
import org.inaetics.dronessimulator.common.protocol.TacticMessage;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.components.engine.Engine;
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
    public static final long ttlLeader = 2; //seconds
    private DroneType droneType;
    private String idLeader;
    private HashMap<String, Tuple<LocalDateTime, List<String>>> teammembers = new HashMap<>();
    private Map<String, D3Vector> mapOfTheWorld = new HashMap<>();
    private ManagedThread handleBroadcastMessagesThread;
    private HashMap<String, D3Vector> targetMoveLocations = new HashMap<>();
    private D3Vector myTargetMoveLocation;
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
        handleBroadcastMessagesThread = new LambdaManagedThread(this::manageIncomingCommunication);
        handleBroadcastMessagesThread.startThread();
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
            log.debug("Find a new leader, the leader was " + idLeader);
            setLeader(null);
            //Leader is not alive
            findLeader();
            //Check if the new found leader is alive. This is done to avoid side-effects of a function
            if (lastRequestForLeader.timeIsExceeded()) {
                //If it could not find a new (alive) leader, just start shooting if possible to any direction.
                if (DroneType.GUN.equals(droneType)) {
                    randomShooting();
                }
            }
        } else if (idLeader.equals(getIdentifier())) {
            sendInstructions();
        }
        if (myTargetMoveLocation != null) {
            moveToLocation(myTargetMoveLocation);
        }
    }

    @Override
    protected void finalizeTactics() {
        handleBroadcastMessagesThread.stopThread();
        log.info("Tactic for " + getIdentifier() + " stopped. This tactic had as leader: " + idLeader + ", and " +
                "myTargetMoveLocation: " + myTargetMoveLocation);
    }

    /**
     * This method handles all the required outgoing communication. All incoming communication should be done in
     * manageIncomingCommunication.
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

    /**
     * This method handles all the required incoming communication. The method is called from within a thread. All
     * outgoing communication should be done in manageOutgoingCommunication.
     */
    private void manageIncomingCommunication() {
        if (radio.getMessages().size() > 0) {
            TacticMessage newMessage = radio.getMessage(TacticMessage.class);
            if (newMessage != null) {
                log.debug("Received a message with type " + String.valueOf(newMessage.get("type")));
                if (MyTacticMessage.checkType(newMessage, HeartbeatMessage.class)) {
                    teammembers.put(newMessage.get("id"), new Tuple<>(LocalDateTime.now(), Arrays.asList(newMessage.get("components").split(","))));
                    mapOfTheWorld.put(newMessage.get("id"), D3Vector.fromString(newMessage.get("position")));
                } else if (MyTacticMessage.checkType(newMessage, InstructionMessage.class)) {
                    if (newMessage.get("receiver").equals(getIdentifier())) {
                        executeInstruction(InstructionMessage.InstructionType.valueOf(newMessage.get(InstructionMessage.
                                InstructionType.class.getSimpleName())), D3Vector.fromString(newMessage.get("target")));
                    }
                } else if (MyTacticMessage.checkType(newMessage, MyTacticMessage.MESSAGETYPES.SearchLeaderMessage) &&
                        droneType.equals(DroneType.RADAR) && idLeader == null) {
                    //Become a leader yourself and prepare
                    setLeader(getIdentifier());
                    radio.send(new DataMessage(this, MyTacticMessage.MESSAGETYPES.IsLeaderMessage).getMessage());
                    log.info("Drone " + getIdentifier() + " is the leader of team " + m_drone.getTeamname());
                } else if (MyTacticMessage.checkType(newMessage, MyTacticMessage.MESSAGETYPES.IsLeaderMessage)) {
                    setLeader(newMessage.get("id"));
                    lastRequestForLeader.reset();
                    targetMoveLocations = new HashMap<>();
                }
            }
        }
    }

    private boolean checkIfLeaderIsAlive() {
        LocalDateTime testingTime = LocalDateTime.now();
        return idLeader != null && teammembers.get(idLeader) != null && !TimeoutTimer.isTimeExceeded(testingTime,
                ttlLeader);
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
                    this.myTargetMoveLocation = targetLocation; //Store the target location to move there if there is no
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
        log.info("Moving to " + location.toString() + " from " + position.toString());
        if (position.distance_between(location) < 1) {
            if (gps.getVelocity().length() != 0) {
                D3Vector move = engine.limit_acceleration(gps.getVelocity().scale(-1));
                log.info("WE ARE CLOSE!" + move.toString());
                engine.changeAcceleration(move);
            }
        } else {
            D3Vector targetAcceleration;
            double distance = gps.getPosition().distance_between(location);
            double decelDistance = (gps.getVelocity().length() * gps.getVelocity().length()) / (2 * Engine.MAX_ACCELERATION);
            if (distance > decelDistance) //we are still far, continue accelerating (if possible)
            {
                targetAcceleration = engine.maximize_acceleration(location.sub(gps.getPosition()));
            } else    //we are about to reach the target, let's start decelerating.
            {
                targetAcceleration = gps.getVelocity().normalize().scale(-(gps.getVelocity().length() * gps
                        .getVelocity()
                        .length()) / (2 * distance));
            }
//            D3Vector move = location.sub(position.add(gps.getVelocity()));
            log.info("WE ARE NOT CLOSE!" + targetAcceleration.toString());
            engine.changeAcceleration(targetAcceleration);
        }
    }

    private void findLeader() {
        if (idLeader == null) {
            log.debug("Searching for leader");
            radio.send(new DataMessage(this, MyTacticMessage.MESSAGETYPES.SearchLeaderMessage).getMessage());
            lastRequestForLeader.reset();
        }
    }

    private void setLeader(String idLeader) {
        log.debug("Setting the leader for " + getIdentifier() + " to " + idLeader);
        this.idLeader = idLeader;
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
        for (String teammember : teammembers.keySet()) {
            D3Vector targetMoveLocation = targetMoveLocations.get(teammember);
            if (targetMoveLocation == null) {
                targetMoveLocation = calculateRandomPositionInField();
                targetMoveLocations.put(teammember, targetMoveLocation);
            }
            log.debug("sendInstructions type: MOVE, to " + teammember + "location: " + targetMoveLocation.toString());
            radio.send(new InstructionMessage(this, InstructionMessage.InstructionType.MOVE, teammember,
                    targetMoveLocation).getMessage());
        }
    }

    private D3Vector calculateRandomPositionInField() {
        return new D3Vector(
                (Math.random() * (Settings.ARENA_WIDTH - 200) + 100),
                (Math.random() * (Settings.ARENA_HEIGHT - 200) + 100),
                (Math.random() * (Settings.ARENA_DEPTH - 200) + 100)
        );
    }

    private int calculateUtility(InstructionMessage.InstructionType type, D3Vector target) {
        int utility = 0;
        for (Map.Entry<String, D3Vector> entry : mapOfTheWorld.entrySet()) {
            if (!teammembers.containsKey(entry.getKey())) {
                utility += entry.getValue().distance_between(target);
            }
        }
        return utility; //TODO
    }

    private enum DroneType {
        GUN, RADAR
    }
}
