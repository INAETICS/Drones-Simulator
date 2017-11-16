package org.inaetics.dronessimulator.drone.tactic;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.common.Settings;
import org.inaetics.dronessimulator.common.Tuple;
import org.inaetics.dronessimulator.common.protocol.TacticMessage;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.tactic.messages.HeartbeatMessage;
import org.inaetics.dronessimulator.drone.tactic.messages.MyTacticMessage;
import org.inaetics.dronessimulator.drone.tactic.messages.RadarImageMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
@NoArgsConstructor //An OSGi constructor
public class TheoreticalTactic extends Tactic {
    private static final long ttlLeader = 2; //seconds
    private DroneType droneType;
    private String idLeader;
    private HashMap<String, LocalDateTime> teammembers = new HashMap<>();
    private Map<String, D3Vector> mapOfTheWorld = new HashMap<>();
    private Thread handleBroadcastMessagesThread;
    private Thread listenToInstructionsThread;
    private D3Vector targetLocation;

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
    void initializeTactics() {
        droneType = getType();
        handleBroadcastMessagesThread = new Thread(this::handleBroadcastMessages);
        handleBroadcastMessagesThread.start();
        listenToInstructionsThread = new Thread(this::listenToInstructions);
        listenToInstructionsThread.start();
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
        targetLocation = new D3Vector(Math.random() * Settings.ARENA_WIDTH, Math.random() * Settings.ARENA_HEIGHT, Math.random() * Settings.ARENA_DEPTH);
        radio.sendText("Move " + getIdentifier() + " to " + targetLocation.toString());
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
    void calculateTactics() {
        manageOutgoingCommunication();
        moveToRandomLocation();
    }

    private void moveToRandomLocation() {
        D3Vector position = gps.getPosition();
        if (position.distance_between(targetLocation) < 1) {
            if (gps.getVelocity().length() != 0) {
                engine.changeAcceleration(new D3Vector());
            }
        }
        D3Vector move = targetLocation.sub(position.add(gps.getVelocity()));
        engine.changeAcceleration(move);
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

    private void handleBroadcastMessages() {
        TacticMessage newMessage = radio.getMessage(TacticMessage.class);
        if (newMessage != null) {
            log.debug("Received a message with type " + String.valueOf(newMessage.get("type")));
            if (HeartbeatMessage.class.getName().equals(newMessage.get("type"))) {
                teammembers.put(newMessage.get("id"), LocalDateTime.now());
                mapOfTheWorld.put(newMessage.get("id"), D3Vector.fromString(newMessage.get("position")));
                switch (droneType) {
                    case GUN:
                        break;
                    case RADAR:

                        break;
                }
            }
        }
    }

    private boolean checkIfLeaderIsAlive() {
        return teammembers.get(idLeader) != null && teammembers.get(idLeader).isBefore(LocalDateTime.now().minusSeconds(ttlLeader));
    }

    private void listenToInstructions() {
        if (checkIfLeaderIsAlive()) {
            //TODO
        } else {
            //Leader is not alive
            findLeader();
            //Check if the new found leader is alive. This is done to avoid side-effects of a function
            if (!checkIfLeaderIsAlive()) {
                //If it could not find a new (alive) leader, just start shooting if possible to any direction.
                if (DroneType.GUN.equals(droneType)) {
                    randomShooting();
                }
            }
        }
    }

    private void findLeader() {
        //TODO
        idLeader = "found leader id";
    }

    private void randomShooting() {
        //TODO
        gun.fireBullet(new D3Vector().toPoolCoordinate());
    }

    private void sendRadarimage() {
        List<Tuple<String, D3Vector>> currentRadar = radar.getRadar();
        RadarImageMessage radarImageMessage = new RadarImageMessage(this, currentRadar);
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

    private int calculateUtility() {
        return 0; //TODO
    }

    private enum DroneType {
        GUN, RADAR
    }
}
