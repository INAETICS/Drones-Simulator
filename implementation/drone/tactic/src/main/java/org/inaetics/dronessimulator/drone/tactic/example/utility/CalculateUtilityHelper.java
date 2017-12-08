package org.inaetics.dronessimulator.drone.tactic.example.utility;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.common.Settings;
import org.inaetics.dronessimulator.common.Tuple;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.tactic.example.utility.messages.InstructionMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Log4j
@RequiredArgsConstructor
public class CalculateUtilityHelper {
    static final int MOVE_GENERATION_DELTA = 1;
    private static final double MAX_ARENA_DISTANCE = new D3Vector(Settings.ARENA_WIDTH, Settings.ARENA_DEPTH, Settings.ARENA_HEIGHT).length();
    private static final double SHOOTING_WEIGHT = 1;
    private static final double MOVING_WEIGHT = 1;
    private static final double MINIMAL_TEAM_DISTANCE = 15d;
    private final CalculateUtilityParams params;

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
        //Do some pre-checks first
        if (params.type.equals(InstructionMessage.InstructionType.SHOOT) && !params.droneHasComponent("gun")) {
            return -1; //We cannot shoot, so all utility should be negative
        }

        if (params.type.equals(InstructionMessage.InstructionType.SHOOT) && params.target.equals(params.getDroneLocation())) {
            return -1; //Do not shoot at yourself
        }

        if (params.type.equals(InstructionMessage.InstructionType.MOVE) && !insideRange(D3Vector.ZERO,
                new D3Vector(Settings.ARENA_WIDTH, Settings.ARENA_DEPTH, Settings.ARENA_HEIGHT), params.target)) {
            return -1; //We never want to move to a location that is out of the bounds of the game
        }

        //Now really calculate the utility, no more early returns.
        final int[] utility = {0}; //A single element array for lambdas as it eeds to be final.
        //Drones that are close have a high likelyhood to kill you, so shoot if possible and move in the opposite direction
        forEachEnemy(enemy -> {
            if (params.type.equals(InstructionMessage.InstructionType.SHOOT)) {
                //Shooting at the closest enemy gives the highest utility
                if (params.target.equals(enemy.getRight())) //If the target to shoot is at the same position as the enemy
                    utility[0] += (MAX_ARENA_DISTANCE - params.target.distance_between(params.getDroneLocation())) * SHOOTING_WEIGHT;
            } else {
                double distanceToEnemy = enemy.getRight().distance_between(params.target);
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
                double distanceToTeammate = teammember.getValue().getLeft().distance_between(params.target);
                if (params.type.equals(InstructionMessage.InstructionType.MOVE)) {
                    //we do not want to crash into a teammate
                    if (distanceToTeammate < MINIMAL_TEAM_DISTANCE) {
                        utility[0] = -1;
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
        return utility[0];
    }

    private void forEachEnemy(Consumer<? super Tuple<String, D3Vector>> f) {
        params.mapOfTheWorld.entrySet().parallelStream()
                .filter(e -> !params.teammembers.containsKey(e.getKey()))
                .map(e -> new Tuple<>(e.getKey(), e.getValue().getRight()))
                .forEach(f);
    }

    private void forEachTeammember(Consumer<? super Map.Entry<String, Tuple<D3Vector, List<String>>>> f) {
        params.teammembers.entrySet().parallelStream().forEach(f);
    }

    /**
     * This is a data object that holds all the required parameters to calculate the utility of a move. This is useful to reduce the number of parameters given to the
     * calculate utility function. This will hopefully make the call to the function more readable.
     */
    @Data
    @RequiredArgsConstructor
    static class CalculateUtilityParams {
        private final Map<String, Tuple<D3Vector, List<String>>> teammembers;
        private final Map<String, Tuple<LocalDateTime, D3Vector>> mapOfTheWorld;
        private final InstructionMessage.InstructionType type;
        private final String droneId;
        private final D3Vector target;

        D3Vector getDroneLocation() {
            return mapOfTheWorld.get(droneId).getRight();
        }

        boolean droneHasComponent(String component) {
            return teammembers.get(droneId).getRight().contains(component);
        }
    }
}
