package org.inaetics.dronessimulator.drone.tactic;

import org.inaetics.dronessimulator.common.Settings;
import org.inaetics.dronessimulator.common.Tuple;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.components.engine.Engine;
import org.inaetics.dronessimulator.drone.components.gps.GPS;
import org.inaetics.dronessimulator.drone.components.gun.Gun;
import org.inaetics.dronessimulator.drone.components.radar.Radar;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simple tactic which flies randomly and fires a bullet on enemies.
 */
public class SimpleTactic extends Tactic {
    protected volatile Radar m_radar;
    protected volatile GPS m_gps;
    protected volatile Engine m_engine;
    protected volatile Gun m_gun;

    private static final double MAX_DEVIATION_POSTION = Settings.ARENA_WIDTH;
    private static final double MAX_Z_DEVIATION_POSTION = Settings.ARENA_HEIGHT;
    /**
     *  -- IMPLEMENT FUNCTIONS
     */

    void calculateTactics(){
        this.calculateAcceleration();
        this.calculateGun();
    }

    /**
     *  -- FUNCTIONS
     */

    /**
     * Accelerate the drone when the current acceleration is 0 m/s.
     * @param input_acceleration the current acceleration
     * @return the new acceleration
     */
    private D3Vector accelerateByNoMovement(D3Vector input_acceleration){
        D3Vector output_acceleration = input_acceleration;
        if (m_gps.getAcceleration().length() == 0 && m_gps.getVelocity().length() == 0){

            double x = ThreadLocalRandom.current().nextDouble(-Engine.MAX_ACCELERATION, Engine.MAX_ACCELERATION);
            double y = ThreadLocalRandom.current().nextDouble(-Engine.MAX_ACCELERATION, Engine.MAX_ACCELERATION);
            double z = ThreadLocalRandom.current().nextDouble(-Engine.MAX_ACCELERATION, Engine.MAX_ACCELERATION);
            output_acceleration =  new D3Vector(x, y, z);
        }
        return output_acceleration;
    }

    /**
     * Change acceleration to avoid wall collision
     * @param input_acceleration the current acceleration
     * @return the new acceleration
     */
    private D3Vector brakeForWall(D3Vector input_acceleration){
        D3Vector output_acceleration = input_acceleration;
        double aantal_seconden_tot_nul = m_gps.getVelocity().length() / Engine.MAX_ACCELERATION;
        D3Vector berekende_position = m_gps.getVelocity().scale(0.5).scale(aantal_seconden_tot_nul).add(m_gps.getPosition());

        if(berekende_position.getX() >= MAX_DEVIATION_POSTION ||
                berekende_position.getX() <= 0 ||
                berekende_position.getY() >= MAX_DEVIATION_POSTION ||
                berekende_position.getY() <= 0 ||
                berekende_position.getZ() >= MAX_Z_DEVIATION_POSTION ||
                berekende_position.getZ() <= 0){
            output_acceleration = m_engine.maximize_acceleration(m_engine.limit_acceleration(m_gps.getVelocity().scale(-1)));
        }
        return output_acceleration;
    }

    /**
     * Change direction of drone when the maximum derivation is archieved.
     * @param input_acceleration the current acceleration
     * @return the new acceleration
     */
    private D3Vector accelerateAfterWall(D3Vector input_acceleration){
        D3Vector output_acceleration = input_acceleration;
        // Check positions | if maximum deviation is archieved then change acceleration in opposite direction
        double x = output_acceleration.getX();
        double y = output_acceleration.getY();
        double z = output_acceleration.getZ();

        if(m_gps.getPosition().getX() >= MAX_DEVIATION_POSTION ||
                m_gps.getPosition().getX() <= 0){
            y = ThreadLocalRandom.current().nextDouble(-Engine.MAX_ACCELERATION, Engine.MAX_ACCELERATION);
            z = ThreadLocalRandom.current().nextDouble(-Engine.MAX_ACCELERATION, Engine.MAX_ACCELERATION);

            if(m_gps.getPosition().getX() >= MAX_DEVIATION_POSTION){
                x = -Engine.MAX_ACCELERATION;
            }
            else if(m_gps.getPosition().getX() <= 0){
                x = Engine.MAX_ACCELERATION;
            }
        }

        if(m_gps.getPosition().getY() >= MAX_DEVIATION_POSTION || m_gps.getPosition().getY() <= 0){
            x = ThreadLocalRandom.current().nextDouble(-Engine.MAX_ACCELERATION, Engine.MAX_ACCELERATION);
            z = ThreadLocalRandom.current().nextDouble(-Engine.MAX_ACCELERATION, Engine.MAX_ACCELERATION);

            if(m_gps.getPosition().getY() >= MAX_DEVIATION_POSTION){
                y = -Engine.MAX_ACCELERATION;
            }
            else if(m_gps.getPosition().getY() <= 0){
                y = Engine.MAX_ACCELERATION;
            }
        }

        if(m_gps.getPosition().getZ() >= MAX_Z_DEVIATION_POSTION || m_gps.getPosition().getZ() <= 0){
            x = ThreadLocalRandom.current().nextDouble(-Engine.MAX_ACCELERATION, Engine.MAX_ACCELERATION);
            y = ThreadLocalRandom.current().nextDouble(-Engine.MAX_ACCELERATION, Engine.MAX_ACCELERATION);

            if(m_gps.getPosition().getZ() >= MAX_Z_DEVIATION_POSTION){
                z = -Engine.MAX_ACCELERATION;
            }
            else if(m_gps.getPosition().getZ() <= 0){
                z = Engine.MAX_ACCELERATION;
            }
        }

        output_acceleration = new D3Vector(x,y,z);
        output_acceleration = m_engine.maximize_acceleration(output_acceleration);
        return output_acceleration;
    }

    /**
     * Calculates the new acceleration of the drone
     */
    private void calculateAcceleration(){
        D3Vector output_acceleration = m_engine.maximize_acceleration(m_gps.getAcceleration());
        output_acceleration = this.accelerateByNoMovement(output_acceleration);
        output_acceleration = this.brakeForWall(output_acceleration);
        output_acceleration = this.accelerateAfterWall(output_acceleration);
        m_engine.changeAcceleration(output_acceleration);
    }

    /**
     * Checks if a bullet can be fired by the gun.
     */
    private void calculateGun(){
        Optional<Tuple<String, D3Vector>> target = m_radar.getNearestTarget();
        if(target.isPresent()){
            if(target.get().getRight().distance_between(m_gps.getPosition()) <= m_gun.getMaxDistance()){
                m_gun.fireBullet(target.get().getRight().sub(m_gps.getPosition()).toPoolCoordinate());
            }
        }
    }


}
