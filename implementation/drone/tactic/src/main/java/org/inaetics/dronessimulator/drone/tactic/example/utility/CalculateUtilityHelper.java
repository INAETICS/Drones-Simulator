package org.inaetics.dronessimulator.drone.tactic.example.utility;

import org.inaetics.dronessimulator.common.Settings;
import org.inaetics.dronessimulator.common.Tuple;
import org.inaetics.dronessimulator.common.model.Triple;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.tactic.example.utility.messages.InstructionMessage;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CalculateUtilityHelper {
    static final int MOVE_GENERATION_DELTA = 1;
    private static final double MAX_ARENA_DISTANCE = new D3Vector(Settings.ARENA_WIDTH, Settings.ARENA_DEPTH, Settings.ARENA_HEIGHT).length();
    private static final double SHOOTING_WEIGHT = 1;
    private static final double MOVING_WEIGHT = 1;
    private static final double MINIMAL_TEAM_DISTANCE = 15d;
    private final CalculateUtilityParams params;

    public CalculateUtilityHelper(CalculateUtilityParams params) {
        this.params = params;
    }

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(CalculateUtilityHelper.class);

    private static boolean insideRange(D3Vector startRange, D3Vector endRange, D3Vector testedLocation) {
        return
                //Check the x location
                testedLocation.getX() > startRange.getX() &&
                        testedLocation.getX() < endRange.getX() &&
                        //Check the y location
                        testedLocation.getY() > startRange.getY() &&
                        testedLocation.getY() < endRange.getY() &&
                        //Check the z location
                        testedLocation.getZ() > startRange.getZ() &&
                        testedLocation.getZ() < endRange.getZ();
    }

    /**
     * Really calculate the utility here. It is based on the following statements
     * - If we do not have a gun, we cannot shoot -> utility becomes -1
     * - Do not shoot yourself -> utility becomes -1
     * - Do not move to a location that is outside the game area -> utility becomes -1
     * - We would like to shoot the enemy that is closest.
     * - We would like to move away from enemies if we cannot shoot it.
     * - We would like to move towards enemies if we can shoot it because it will improve our accuracy.
     * - We would like to move close to a team member, but not fly into it.
     * - We do NOT want to shoot team members.
     *
     * @return the utility as an integer
     */
    public Integer calculateUtility() {
        long starttime = System.currentTimeMillis();
        //Do some pre-checks first
        if (params.type.equals(InstructionMessage.InstructionType.SHOOT) && !params.droneHasComponent("gun")) {
            return -1; //We cannot shoot, so all utility should be negative
        }

        if (params.type.equals(InstructionMessage.InstructionType.SHOOT) && params.target.equals(params.getDroneLocation())) {
            return -1; //Do not shoot at yourself
        }

        if (params.type.equals(InstructionMessage.InstructionType.MOVE) && !insideRange(D3Vector.ZERO, Settings.ARENA, params.target)) {
            return -1; //We never want to move to a location that is out of the bounds of the game
        }

        //Now really calculate the utility, no more early returns.
        final int[] utility = {0}; //A single element array for lambdas as it needs to be final.
        final boolean[] thereAreEnemies = {false};
        //Drones that are close have a high likelyhood to kill you, so shoot if possible and move in the opposite direction
        forEachEnemy(enemy -> {
            thereAreEnemies[0] = true;
            if (params.type.equals(InstructionMessage.InstructionType.SHOOT)) {
                //Shooting at the closest enemy gives the highest utility
                if (params.target.equals(enemy)) //If the target to shoot is at the same position as the enemy
                    utility[0] += (MAX_ARENA_DISTANCE - params.target.distance_between(params.getDroneLocation())) * SHOOTING_WEIGHT;
            } else {
                double distanceToEnemy = enemy.distance_between(params.target);
                if (params.droneHasComponent("gun")) {
                    //Moving towards a target when you can shoot it, is a good idea, so the utility is bigger if
                    // we move towards the enemy.
                    utility[0] += (MAX_ARENA_DISTANCE - distanceToEnemy) * MOVING_WEIGHT;
                } else {
                    //We cannot shoot it, so evade it.
                    utility[0] += (distanceToEnemy * MOVING_WEIGHT);
                }
            }
        });
        forEachTeammember(teammember -> {
            if (!teammember.getKey().equals(params.droneId)) { //If the teammember is not the current drone
                double distanceToTeammate = teammember.getValue().getB().distance_between(params.target);
                if (params.type.equals(InstructionMessage.InstructionType.MOVE)) {
                    //we do not want to crash into a teammate
                    if (distanceToTeammate < MINIMAL_TEAM_DISTANCE) {
                        utility[0] = -1;
                    } else if (!thereAreEnemies[0]) {
                        utility[0] += params.target.distance_between(params.getDroneLocation()) * MOVING_WEIGHT;
                    } else {
                        //Move with teammates over moving alone
                        utility[0] += (MAX_ARENA_DISTANCE - (int) distanceToTeammate) * MOVING_WEIGHT;
                    }
                } else {
                    //Do not shoot at teammembers
                    if (distanceToTeammate < 1) {
                        utility[0] = -1;
                    }
                }
            }
        });
        log.debug("Utility calculations took:" + (System.currentTimeMillis() - starttime));
        return utility[0];
    }

    void forEachEnemy(Consumer<? super D3Vector> f) {
        params.enemies.parallelStream()
                .peek(e -> log.debug("Found enemy at: " + e.toString() + " This is my set of teammembers: " + params.teammembers + " This is the full radar: " + params.radarImage))
                .forEach(f);
    }

    private void forEachTeammember(Consumer<? super Map.Entry<String, Triple<LocalDateTime, D3Vector, List<String>>>> f) {
        params.teammembers.entrySet().parallelStream().forEach(f);
    }

    /**
     * This is a data object that holds all the required parameters to calculate the utility of a move. This is useful to reduce the number of parameters given to the
     * calculate utility function. This will hopefully make the call to the function more readable.
     */

    static class CalculateUtilityParams {
        private final Map<String, Triple<LocalDateTime, D3Vector, List<String>>> teammembers;
        private final Collection<Tuple<LocalDateTime, D3Vector>> radarImage;
        private final InstructionMessage.InstructionType type;
        private final String droneId;
        private final D3Vector target;
        private final Collection<D3Vector> enemies;

        CalculateUtilityParams(Map<String, Triple<LocalDateTime, D3Vector, List<String>>> teammembers, Queue<Tuple<LocalDateTime, D3Vector>> radarImage, InstructionMessage.InstructionType type, String droneId, D3Vector target) {
            this.teammembers = Collections.unmodifiableMap(teammembers);
            this.radarImage = Collections.unmodifiableCollection(radarImage);
            this.enemies = getEnemies(this.teammembers, this.radarImage);
            this.type = type;
            this.droneId = droneId;
            this.target = target;
        }

        private Collection<D3Vector> getEnemies(Map<String, Triple<LocalDateTime, D3Vector, List<String>>> teammembers, Collection<Tuple<LocalDateTime, D3Vector>> radarImage) {
            List<D3Vector> enemiesList = radarImage.parallelStream()
                    .map(Tuple::getRight) //Only get the positions
                    .filter(ral -> teammembers.entrySet().parallelStream().map(tm -> tm.getValue().getB()) //Get positions of the teammembers
                            .filter(t -> t.distance_between(ral) < Settings.MAX_DRONE_VELOCITY).count() == 0) //If there is a teammember close, it must be from that drone, so we only want the
                    // ones that
                    // do NOT have a teammember close
                    .collect(Collectors.toList());
            log.debug("Number of expected enemies(" + radarImage.size() + "+" + teammembers.size() + "): " + (radarImage.size() - teammembers.size()) + ". Number of found enemies" + enemiesList.size());
            return enemiesList;
        }

        D3Vector getDroneLocation() {
            return teammembers.get(droneId).getB();
        }

        @SuppressWarnings("SameParameterValue")
        boolean droneHasComponent(String component) {
            return teammembers.get(droneId).getC().contains(component);
        }
    }
}
