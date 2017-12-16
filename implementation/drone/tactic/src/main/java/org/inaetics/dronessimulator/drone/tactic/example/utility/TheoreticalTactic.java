package org.inaetics.dronessimulator.drone.tactic.example.utility;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.common.*;
import org.inaetics.dronessimulator.common.model.Triple;
import org.inaetics.dronessimulator.common.protocol.TacticMessage;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.tactic.Tactic;
import org.inaetics.dronessimulator.drone.tactic.example.utility.messages.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.inaetics.dronessimulator.drone.tactic.example.utility.CalculateUtilityHelper.MOVE_GENERATION_DELTA;

@Log4j
@NoArgsConstructor //An OSGi constructor
public class TheoreticalTactic extends Tactic {
    public static final double TTL_DRONE = 3 * Settings.getTickTime(ChronoUnit.SECONDS); //seconds
    private DroneType droneType;
    private String idLeader;
    /**
     * This is a list of the teammembers based on the heartbeat messages
     * <p>
     * The key is the id of the drone
     * The left item of the tuple is the location of the teammember
     * The right list of the tuple is a list of available components
     */
    private final Map<String, Triple<LocalDateTime, D3Vector, List<String>>> teammembers = new ConcurrentHashMap<>();
    private Queue<Tuple<LocalDateTime, D3Vector>> radarImage = new ConcurrentLinkedQueue<>();
    private ManagedThread handleBroadcastMessagesThread;
    private D3Vector myTargetMoveLocation;
    private final TimeoutTimer lastRequestForLeader = new TimeoutTimer(1000); //1 sec

    private DroneType getType() {
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
        if (DroneType.GUN.equals(droneType)) {
                //Send a message if you fire a bullet
                gun.registerCallback((fireBulletMessage) -> {
                    DataMessage shotMessage = new DataMessage(this, MyTacticMessage.MESSAGETYPES.FIRED_BULLET_MESSAGE);
                    shotMessage.getData().put("direction", String.valueOf(fireBulletMessage.getDirection().orElse(null)));
                    shotMessage.getData().put("velocity", String.valueOf(fireBulletMessage.getVelocity().orElse(null)));
                    shotMessage.getData().put("firedMoment", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
                    radio.send(shotMessage.getMessage());
                });
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
        //Remove stale data from the map
        radarImage.removeIf(e -> TimeoutTimer.isTimeExceeded(e.getLeft(), TTL_DRONE));
        teammembers.entrySet().removeIf(e -> TimeoutTimer.isTimeExceeded(e.getValue().getA(), TTL_DRONE));

        manageOutgoingCommunication();

        //Check leader
        if (!checkIfLeaderIsAlive()) {
            log.debug("Find a new leader, the leader was " + idLeader);
            moveEvasively();
            if (idLeader != null)
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

        //Keep moving if we have a target move location. Otherwise we will overshoot our target since acceleration corrections are required.
        if (myTargetMoveLocation != null) {
            moveToLocation(myTargetMoveLocation);
        }
    }

    private void moveEvasively() {
        executeInstruction(InstructionMessage.InstructionType.MOVE, getRandomLocation());
    }

    private D3Vector getRandomLocation() {
        return new D3Vector(Math.random() * Settings.ARENA_WIDTH, Math.random() * Settings.ARENA_HEIGHT, Math.random() * Settings.ARENA_DEPTH);
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
     * This method handles all the required incoming communication. The method is called from within a thread.
     * All outgoing communication should be done in manageOutgoingCommunication.
     */
    private void manageIncomingCommunication() {
        if (!radio.getMessages().isEmpty()) {
            TacticMessage newMessage = radio.getMessage(TacticMessage.class);
            if (newMessage != null) {
                log.debug("Received a message with type " + String.valueOf(newMessage.get("type")));
                //@formatter:off
                if (MyTacticMessage.checkType(newMessage,
        HeartbeatMessage.class)) {
                    teammembers.put(newMessage.get("id"), new Triple<>(LocalDateTime.now(), D3Vector.fromString(newMessage.get("position")), Arrays.asList(newMessage.get("components").split(","))));
                    if (Boolean.parseBoolean(newMessage.get("isLeader")) && !newMessage.get("id").equals(idLeader)) {
                        setLeader(newMessage.get("id"));
                    }
                } else if (MyTacticMessage.checkType(newMessage,
        RadarImageMessage.class)) {
                    radarImage = RadarImageMessage.parseData(newMessage).parallelStream().map((e) -> new Tuple<>(LocalDateTime.now(), e)).collect(Collectors.toCollection(ConcurrentLinkedQueue::new));
                } else if (MyTacticMessage.checkType(newMessage,
        InstructionMessage.class)) {
                    if (newMessage.get("receiver").equals(getIdentifier())) {
                        executeInstruction(InstructionMessage.InstructionType.valueOf(newMessage.get(InstructionMessage.InstructionType.class.getSimpleName())),
                                D3Vector.fromString(newMessage.get("target")));
                    }
                } else if (MyTacticMessage.checkType(newMessage,
        MyTacticMessage.MESSAGETYPES.SEARCH_LEADER_MESSAGE
                ) && droneType.equals(DroneType.RADAR) && idLeader == null) {
                    //Become a leader yourself and prepare
                    setLeader(getIdentifier());
                    radio.send(new DataMessage(this, MyTacticMessage.MESSAGETYPES.IS_LEADER_MESSAGE).getMessage());
                    log.info("Drone " + getIdentifier() + " is the leader of team " + m_drone.getTeamname());
                } else if (MyTacticMessage.checkType(newMessage,
        MyTacticMessage.MESSAGETYPES.IS_LEADER_MESSAGE)) {
                    setLeader(newMessage.get("id"));
                    lastRequestForLeader.reset();
                }
                //@formatter:on
            }
        }
    }

    private boolean checkIfLeaderIsAlive() {
        return idLeader != null && teammembers.get(idLeader) != null && !TimeoutTimer.isTimeExceeded(teammembers.get(idLeader).getA(), TTL_DRONE);
    }

    private void executeInstruction(InstructionMessage.InstructionType instructionType, D3Vector targetLocation) {
        log.info("Execute instruction " + instructionType + " with target " + String.valueOf(targetLocation) + ". Current location: " + String.valueOf(gps.getPosition()));
        switch (instructionType) {
            case SHOOT:
                if (hasComponents("gun")) {
                    gun.fireBullet(targetLocation.sub(gps.getPosition()).toPoolCoordinate());
                } else {
                    log.error("Could not execute instruction " + instructionType + " with target " + String.valueOf(targetLocation));
                }
                break;
            case MOVE:
                if (hasComponents("engine", "gps")) {
                    this.myTargetMoveLocation = targetLocation; //Store the target location to move there if there is no new instruction.
                    moveToLocation(targetLocation);
                } else {
                    log.error("Could not execute instruction " + instructionType + " with target " + String.valueOf(targetLocation));
                }
                break;
        }
    }

    private void moveToLocation(D3Vector location) {
        //TODO replace with better move function
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
            double decelDistance = (gps.getVelocity().length() * gps.getVelocity().length()) / (2 * Settings
                    .MAX_DRONE_ACCELERATION);
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
//        engine.moveTo(location);
    }

    private void findLeader() {
        if (idLeader == null) {
            log.debug("Searching for leader");
            radio.send(new DataMessage(this, MyTacticMessage.MESSAGETYPES.SEARCH_LEADER_MESSAGE).getMessage());
            lastRequestForLeader.reset();
        }
    }

    private void setLeader(String idLeader) {
        log.debug("Setting the leader for " + getIdentifier() + " to " + idLeader);
        this.idLeader = idLeader;
    }

    private void randomShooting() {
        List<D3Vector> targets = new LinkedList<>();
        if (radarImage.size() > 1) {
            //This is an unlikely case since anybody can become a leader, but this is a fallback.
            CalculateUtilityHelper helper = new CalculateUtilityHelper(new CalculateUtilityHelper.CalculateUtilityParams(teammembers, radarImage, InstructionMessage.InstructionType.SHOOT,
                    getIdentifier(), null));
            helper.forEachEnemy(targets::add);
        }
        log.debug("RANDOM SHOOTING AND MOVING!!!");
        if (targets.isEmpty()) {
            targets.add(getRandomLocation());
        }
        executeInstruction(InstructionMessage.InstructionType.SHOOT, targets.get(0));
        executeInstruction(InstructionMessage.InstructionType.MOVE, targets.get(0));
    }

    private void sendRadarimage() {
        List<D3Vector> currentRadar = radar.getRadar();
        currentRadar.add(gps.getPosition());
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
        teammembers.entrySet().parallelStream().forEach(teammember -> {
            Map<Tuple<InstructionMessage.InstructionType, D3Vector>, Integer> utilityMapMove = new HashMap<>();
            Map<Tuple<InstructionMessage.InstructionType, D3Vector>, Integer> utilityMapShoot = new HashMap<>();

            //Generate utility calculations to move in any of the 26 directions
            generateUtilityCalculations(utilityMapMove, InstructionMessage.InstructionType.MOVE, teammember.getKey());
            //Generate utility for movement towards a different drone and shooting towards a drone
            radarImage.parallelStream().forEach(entry -> {
                utilityMapMove.put(
                        new Tuple<>(InstructionMessage.InstructionType.MOVE, entry.getRight()),
                        calculateUtility(
                                InstructionMessage.InstructionType.MOVE,
                                teammember.getKey(),
                                entry.getRight()
                        )
                );

                if (teammember.getValue().getC().contains("gun")) {
                    utilityMapShoot.put(
                            new Tuple<>(InstructionMessage.InstructionType.SHOOT, entry.getRight()),
                            calculateUtility(
                                    InstructionMessage.InstructionType.SHOOT,
                                    teammember.getKey(),
                                    entry.getRight()
                            )
                    );
                }
            });
            Optional<Entry<Tuple<InstructionMessage.InstructionType, D3Vector>, Integer>> highestUtilityShoot =
                    utilityMapShoot.entrySet().parallelStream().max(Comparator.comparingInt(Entry::getValue));
            if (highestUtilityShoot.isPresent() && highestUtilityShoot.get().getValue() > 0) {
                log.debug("ShootUtility:" + highestUtilityShoot.get() + " out of " + utilityMapShoot);
                Tuple<InstructionMessage.InstructionType, D3Vector> highesUtilityParams = highestUtilityShoot.get().getKey();
                radio.send(new InstructionMessage(this, highesUtilityParams.getLeft(), teammember.getKey(), highesUtilityParams.getRight()).getMessage());
            }

            Optional<Entry<Tuple<InstructionMessage.InstructionType, D3Vector>, Integer>> highestUtilityMove =
                    utilityMapMove.entrySet().parallelStream().max(Comparator.comparingInt(Entry::getValue));
            if (highestUtilityMove.isPresent() && highestUtilityMove.get().getValue() > 0) {
                log.debug("MoveUtility:" + highestUtilityMove.get() + " out of " + utilityMapMove);
                Tuple<InstructionMessage.InstructionType, D3Vector> highesUtilityParams = highestUtilityMove.get().getKey();
                radio.send(new InstructionMessage(this, highesUtilityParams.getLeft(), teammember.getKey(), highesUtilityParams.getRight()).getMessage());
            }

        });
    }

    /**
     * Use three for loops to walk over all neighboring positions of the current position and calculate the utility for that location.
     *
     * @param utilityMap the map to which the utility is added
     * @param type       the instruction type for which we need to calculate the utility
     * @param droneId    the id of the drone for which we will generate all this
     */
    private void generateUtilityCalculations(Map<Tuple<InstructionMessage.InstructionType, D3Vector>, Integer> utilityMap, InstructionMessage.InstructionType type,
                                             String droneId) {
        D3Vector currentLocation = teammembers.get(droneId).getB();

        IntStream.range(-MOVE_GENERATION_DELTA, MOVE_GENERATION_DELTA).parallel().forEach(x ->
                IntStream.range(-MOVE_GENERATION_DELTA, MOVE_GENERATION_DELTA).parallel().forEach(y ->
                        IntStream.range(-MOVE_GENERATION_DELTA, MOVE_GENERATION_DELTA).parallel()
                                .mapToObj(z -> currentLocation.add(new D3Vector(x, y, z)))
                                .forEach(targetLocation -> utilityMap.put(
                                        new Tuple<>(type, targetLocation),
                                        calculateUtility(type, droneId, targetLocation)
                                ))
                )
        );
    }

    /**
     * Helper function that wraps the parameters into an object and calls the calculate utility on the correct object.
     */
    int calculateUtility(InstructionMessage.InstructionType instructionType, String droneId, D3Vector target) {
        CalculateUtilityHelper.CalculateUtilityParams params = new CalculateUtilityHelper.CalculateUtilityParams(teammembers, radarImage, instructionType, droneId, target);
        return new CalculateUtilityHelper(params).calculateUtility();
    }

    private enum DroneType {
        GUN, RADAR
    }
}
