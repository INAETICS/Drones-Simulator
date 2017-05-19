package org.inaetics.dronessimulator.drone.tactic;

import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.drone.components.engine.Engine;
import org.inaetics.dronessimulator.drone.components.gps.GPS;
import org.inaetics.dronessimulator.drone.components.radar.Radar;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by mart on 17-5-17.
 */
public class SimpleTactic extends Tactic {
    protected volatile Radar m_radar;
    protected volatile GPS m_gps;
    protected  volatile Engine m_engine;

    private static final int MAX_DEVIATION_POSTION = 400;
    private static final int MAX_VELOCITY = 20;
    private static final int MAX_ACCELERATION = 10;



    /**
     *  -- FUNCTIONS
     */
    void recalculateAcceleration(){
        D3Vector output_acceleration = m_gps.getAcceleration();
        output_acceleration = m_engine.maximize_acceleration(m_gps.getAcceleration());

        //
        if (m_gps.getAcceleration().length() == 0 && m_gps.getVelocity().length() == 0){
            double x = ThreadLocalRandom.current().nextDouble(-MAX_ACCELERATION, MAX_ACCELERATION);
            double y = ThreadLocalRandom.current().nextDouble(-MAX_ACCELERATION, MAX_ACCELERATION);
            double z = ThreadLocalRandom.current().nextDouble(-MAX_ACCELERATION, MAX_ACCELERATION);
            output_acceleration = new D3Vector(x, y, z);
        }

        // Check velocity
        if (m_gps.getVelocity().length() >= MAX_VELOCITY){
            output_acceleration = new D3Vector();
        }

        // Change acceleration if velocity is close to the maximum velocity
        if (m_gps.getVelocity().length() >= (MAX_VELOCITY - (MAX_VELOCITY * 0.1))) {
            double factor = 0.25;
            output_acceleration = m_gps.getAcceleration().scale(factor);
        }

        double aantal_seconden_tot_nul = m_gps.getVelocity().length() / MAX_ACCELERATION;
        D3Vector berekende_vertraging = m_engine.maximize_acceleration(m_engine.limit_acceleration(m_gps.getVelocity().scale(-1)));
        D3Vector berekende_position = m_gps.getVelocity().scale((1/2) * berekende_vertraging.length() * Math.pow(aantal_seconden_tot_nul, 2)).add(m_gps.getPosition());

        if (Math.abs(berekende_position.getX()-MAX_DEVIATION_POSTION) >= MAX_DEVIATION_POSTION
                || Math.abs(berekende_position.getY()-MAX_DEVIATION_POSTION) >= MAX_DEVIATION_POSTION
                || Math.abs(berekende_position.getZ()-MAX_DEVIATION_POSTION) >= MAX_DEVIATION_POSTION){
            output_acceleration = berekende_vertraging;
        }


        // Check positions | if maximum deviation is archieved then change acceleration in opposite direction
        if (Math.abs(m_gps.getPosition().getX()-MAX_DEVIATION_POSTION) >= MAX_DEVIATION_POSTION
                || Math.abs(m_gps.getPosition().getY()-MAX_DEVIATION_POSTION) >= MAX_DEVIATION_POSTION
                || Math.abs(m_gps.getPosition().getZ()-MAX_DEVIATION_POSTION) >= MAX_DEVIATION_POSTION){


            double x = ThreadLocalRandom.current().nextDouble(-MAX_ACCELERATION, MAX_ACCELERATION);//current_acceleration.getX();
            double y = ThreadLocalRandom.current().nextDouble(-MAX_ACCELERATION, MAX_ACCELERATION);//current_acceleration.getY();
            double z = ThreadLocalRandom.current().nextDouble(-MAX_ACCELERATION, MAX_ACCELERATION);//current_acceleration.getZ();

            if( m_gps.getPosition().getX() >= MAX_DEVIATION_POSTION){
                D3Vector next_postion = m_gps.getPosition().add(m_gps.getVelocity());
                D3Vector next_position_accelerated = m_gps.getPosition().add(m_gps.getVelocity().add(m_gps.getAcceleration()));
                if(next_postion.getX() <= next_position_accelerated.getX()){
                    x = -MAX_ACCELERATION; //ThreadLocalRandom.current().nextDouble(-MAX_ACCELERATION, 0);
                } else{
                    x = m_gps.getAcceleration().getX();
                }
                x = - MAX_ACCELERATION;
            }

            if( (m_gps.getPosition().getX()-MAX_DEVIATION_POSTION) <= -MAX_DEVIATION_POSTION){
                D3Vector next_postion = m_gps.getPosition().add(m_gps.getVelocity());
                D3Vector next_position_accelerated = m_gps.getPosition().add(m_gps.getVelocity().add(m_gps.getAcceleration()));
                if(next_postion.getX() >= next_position_accelerated.getX()){
                    x = MAX_ACCELERATION;//ThreadLocalRandom.current().nextDouble(0, MAX_ACCELERATION);
                } else{
                    x = m_gps.getAcceleration().getX();
                }
                x = MAX_ACCELERATION;
            }
            if( m_gps.getPosition().getY() >= MAX_DEVIATION_POSTION){
                D3Vector next_postion = m_gps.getPosition().add(m_gps.getVelocity());
                D3Vector next_position_accelerated = m_gps.getPosition().add(m_gps.getVelocity().add(m_gps.getAcceleration()));
                if(next_postion.getY() <= next_position_accelerated.getY()){
                    y = -MAX_ACCELERATION; //ThreadLocalRandom.current().nextDouble(-MAX_ACCELERATION, 0);
                } else{
                    y = m_gps.getAcceleration().getY();
                }
                y = -MAX_ACCELERATION;
            }

            if( (m_gps.getPosition().getY()-MAX_DEVIATION_POSTION) <= -MAX_DEVIATION_POSTION){
                D3Vector next_postion = m_gps.getPosition().add(m_gps.getVelocity());
                D3Vector next_position_accelerated = m_gps.getPosition().add(m_gps.getVelocity().add(m_gps.getAcceleration()));
                if(next_postion.getY() >= next_position_accelerated.getY()){
                    y = MAX_ACCELERATION;//ThreadLocalRandom.current().nextDouble(0, MAX_ACCELERATION);
                } else{
                    y = m_gps.getAcceleration().getY();
                }
                y = MAX_ACCELERATION;
            }

            if( m_gps.getPosition().getZ() >= MAX_DEVIATION_POSTION){
                D3Vector next_postion = m_gps.getPosition().add(m_gps.getVelocity());
                D3Vector next_position_accelerated = m_gps.getPosition().add(m_gps.getVelocity().add(m_gps.getAcceleration()));
                if(next_postion.getZ() <= next_position_accelerated.getZ()){
                    z = -MAX_ACCELERATION; //ThreadLocalRandom.current().nextDouble(-MAX_ACCELERATION, 0);
                } else{
                    z = m_gps.getAcceleration().getZ();
                }
                z = -MAX_ACCELERATION;
            }

            if( (m_gps.getPosition().getZ()-MAX_DEVIATION_POSTION) <= -MAX_DEVIATION_POSTION){
                D3Vector next_postion = m_gps.getPosition().add(m_gps.getVelocity());
                D3Vector next_position_accelerated = m_gps.getPosition().add(m_gps.getVelocity().add(m_gps.getAcceleration()));
                if(next_postion.getZ() >= next_position_accelerated.getZ()){
                    z = MAX_ACCELERATION; //ThreadLocalRandom.current().nextDouble(0, MAX_ACCELERATION);
                } else{
                    z = m_gps.getAcceleration().getZ();
                }
                z = MAX_ACCELERATION;
            }

            output_acceleration = new D3Vector(x, y, z);
        }

        output_acceleration = m_engine.limit_acceleration(output_acceleration);
        m_engine.sendAcceleration(output_acceleration);
    }





}
