package org.inaetics.dronessimulator.drone.tactic.example;

import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.common.Settings;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.tactic.Tactic;

import java.util.concurrent.ThreadLocalRandom;

@Log4j
public class MoveToLocationTactic extends Tactic {
    private D3Vector randomLocation;
  
    public MoveToLocationTactic() {
        this.randomLocation = new D3Vector(
                ThreadLocalRandom.current().nextInt(100, (int) Settings.ARENA_WIDTH - 100),
                ThreadLocalRandom.current().nextInt(100, (int) Settings.ARENA_HEIGHT - 100),
                ThreadLocalRandom.current().nextInt(100, (int) Settings.ARENA_DEPTH - 100)
        );
    }

    @Override
    protected void initializeTactics() {
        log.info("Initializing tactics..");
    }

    @Override
    protected void calculateTactics() {
        log.info("calculateTactics.. {}", randomLocation);
        calculateMovement(randomLocation);
    }

    @Override
    protected void finalizeTactics() {
        log.info("Finalizing tactics..");
    }

    public void moveToLocation(D3Vector location) {
        D3Vector position = gps.getPosition();
        log.info(String.format("location: %s", location));
        log.info(String.format("position: %s", position));
        log.info("Moving to " + location.toString() + " from " + position.toString());
        if (position.distance_between(location) < 1) {
            if (gps.getVelocity().length() != 0) {
                D3Vector move = engine.limit_acceleration(gps.getVelocity().scale(-1));
                engine.changeAcceleration(move);
            }
        } else {
            D3Vector targetAcceleration;
            double distance = gps.getPosition().distance_between(location);
            double decelDistance = (gps.getVelocity().length() * gps.getVelocity().length()) / (2 * Settings.MAX_DRONE_ACCELERATION);
            if (distance > decelDistance) //we are still far, continue accelerating (if possible)
            {
                targetAcceleration = engine.maximize_acceleration(location.sub(gps.getPosition()));
            } else    //we are about to reach the target, let's start decelerating.
            {
                targetAcceleration = gps.getVelocity().normalize().scale(-(gps.getVelocity().length() * gps.getVelocity().length()) / (2 * distance));
            }
            //            D3Vector move = location.sub(position.add(gps.getVelocity()));
            engine.changeAcceleration(targetAcceleration);
        }
    }

    public void calculateMovement(D3Vector moveTarget) {
        D3Vector position = gps.getPosition();
        log.info(String.format("location: %s", moveTarget));
        log.info(String.format("position: %s", position));
        log.info("Moving to " + moveTarget.toString() + " from " + position.toString());

        double distance = position.distance_between(moveTarget);
        double velocity = gps.getVelocity().length();

        if (position.distance_between(moveTarget) < 1) {
            if (gps.getVelocity().length() != 0) {
                engine.changeAcceleration(gps.getVelocity().scale(-1));
            }
        } else {
            if (velocity == 0 || position.distance_between(moveTarget) > ((velocity * velocity) / (2 * Settings.MAX_DRONE_ACCELERATION))) {
                engine.changeAcceleration(moveTarget.sub(position.add(gps.getVelocity())));
            } else {
                D3Vector newAcceleration = (moveTarget.sub(position)).normalize().scale(-(velocity * velocity) / (2 * distance));
                engine.changeAcceleration(newAcceleration);
            }
        }
    }
}
