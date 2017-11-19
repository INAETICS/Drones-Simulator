package org.inaetics.dronessimulator.drone.tactic;

import org.inaetics.dronessimulator.common.Settings;
import org.inaetics.dronessimulator.common.Tuple;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.components.engine.Engine;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simple tactic which flies randomly and fires a bullet on enemies.
 */
public class SimpleTactic extends Tactic {

    private static final double MAX_DEVIATION_POSTION = Settings.ARENA_WIDTH;
    private static final double MAX_Z_DEVIATION_POSTION = Settings.ARENA_HEIGHT;

    private List<String> teamMates = new ArrayList<>();
    private LocalDateTime nextpoll = LocalDateTime.now();

    @Override
    protected void initializeTactics() {

    }

    /**
     * -- IMPLEMENT FUNCTIONS
     */

    protected void calculateTactics() {
        this.calculateAcceleration();
        this.calculateGun();
    }

    @Override
    protected void finalizeTactics() {

    }

    /**
     * Accelerate the drone when the current acceleration is 0 m/s.
     *
     * @param input_acceleration the current acceleration
     * @return the new acceleration
     */
    private D3Vector accelerateByNoMovement(D3Vector input_acceleration) {
        D3Vector output_acceleration = input_acceleration;
        if (gps.getAcceleration().length() == 0 && gps.getVelocity().length() == 0) {

            double x = ThreadLocalRandom.current().nextDouble(-Engine.MAX_ACCELERATION, Engine.MAX_ACCELERATION);
            double y = ThreadLocalRandom.current().nextDouble(-Engine.MAX_ACCELERATION, Engine.MAX_ACCELERATION);
            double z = ThreadLocalRandom.current().nextDouble(-Engine.MAX_ACCELERATION, Engine.MAX_ACCELERATION);
            output_acceleration = new D3Vector(x, y, z);
        }
        return output_acceleration;
    }

    /**
     * Change acceleration to avoid wall collision
     *
     * @param input_acceleration the current acceleration
     * @return the new acceleration
     */
    private D3Vector brakeForWall(D3Vector input_acceleration) {
        D3Vector output_acceleration = input_acceleration;
        double aantal_seconden_tot_nul = gps.getVelocity().length() / Engine.MAX_ACCELERATION;
        D3Vector berekende_position = gps.getVelocity().scale(0.5).scale(aantal_seconden_tot_nul).add(gps.getPosition());

        if (berekende_position.getX() >= MAX_DEVIATION_POSTION ||
                berekende_position.getX() <= 0 ||
                berekende_position.getY() >= MAX_DEVIATION_POSTION ||
                berekende_position.getY() <= 0 ||
                berekende_position.getZ() >= MAX_Z_DEVIATION_POSTION ||
                berekende_position.getZ() <= 0) {
            output_acceleration = engine.maximize_acceleration(engine.limit_acceleration(gps.getVelocity().scale(-1)));
        }
        return output_acceleration;
    }

    /**
     * Change direction of drone when the maximum derivation is archieved.
     *
     * @param input_acceleration the current acceleration
     * @return the new acceleration
     */
    private D3Vector accelerateAfterWall(D3Vector input_acceleration) {
        D3Vector output_acceleration = input_acceleration;
        // Check positions | if maximum deviation is archieved then change acceleration in opposite direction
        double x = output_acceleration.getX();
        double y = output_acceleration.getY();
        double z = output_acceleration.getZ();

        if (gps.getPosition().getX() >= MAX_DEVIATION_POSTION ||
                gps.getPosition().getX() <= 0) {
            y = ThreadLocalRandom.current().nextDouble(-Engine.MAX_ACCELERATION, Engine.MAX_ACCELERATION);
            z = ThreadLocalRandom.current().nextDouble(-Engine.MAX_ACCELERATION, Engine.MAX_ACCELERATION);

            if(gps.getPosition().getX() >= MAX_DEVIATION_POSTION){
                x = - Engine.MAX_ACCELERATION;
            }
            else if(gps.getPosition().getX() <= 0){
                x = Engine.MAX_ACCELERATION;
            }
        }

        if (gps.getPosition().getY() >= MAX_DEVIATION_POSTION || gps.getPosition().getY() <= 0) {
            x = ThreadLocalRandom.current().nextDouble(-Engine.MAX_ACCELERATION, Engine.MAX_ACCELERATION);
            z = ThreadLocalRandom.current().nextDouble(-Engine.MAX_ACCELERATION, Engine.MAX_ACCELERATION);

            if(gps.getPosition().getY() >= MAX_DEVIATION_POSTION){
                y = - Engine.MAX_ACCELERATION;
            }
            else if(gps.getPosition().getY() <= 0){
                y = Engine.MAX_ACCELERATION;
            }
        }

        if (gps.getPosition().getZ() >= MAX_Z_DEVIATION_POSTION || gps.getPosition().getZ() <= 0) {
            x = ThreadLocalRandom.current().nextDouble(-Engine.MAX_ACCELERATION, Engine.MAX_ACCELERATION);
            y = ThreadLocalRandom.current().nextDouble(-Engine.MAX_ACCELERATION, Engine.MAX_ACCELERATION);

            if(gps.getPosition().getZ() >= MAX_Z_DEVIATION_POSTION){
                z = - Engine.MAX_ACCELERATION;
            }
            else if(gps.getPosition().getZ() <= 0){
                z = Engine.MAX_ACCELERATION;
            }
        }

        output_acceleration = new D3Vector(x, y, z);
        output_acceleration = engine.maximize_acceleration(output_acceleration);
        return output_acceleration;
    }

    /**
     * Calculates the new acceleration of the drone
     */
    private void calculateAcceleration() {
        D3Vector output_acceleration = engine.maximize_acceleration(gps.getAcceleration());
        output_acceleration = this.accelerateByNoMovement(output_acceleration);
        output_acceleration = this.brakeForWall(output_acceleration);
        output_acceleration = this.accelerateAfterWall(output_acceleration);
        engine.changeAcceleration(output_acceleration);
    }

    /**
     * Checks if a bullet can be fired by the gun.
     */
    private void calculateGun() {
        Optional<Tuple<String, D3Vector>> target = radar.getNearestTarget();
        if (target.isPresent()) {
            if (target.get().getRight().distance_between(gps.getPosition()) <= gun.getMaxDistance()) {
                gun.fireBullet(target.get().getRight().sub(gps.getPosition()).toPoolCoordinate());
            }
        }
    }


}
