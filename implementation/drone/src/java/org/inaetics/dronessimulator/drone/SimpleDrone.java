package org.inaetics.dronessimulator.drone;
import org.inaetics.dronessimulator.common.*;

import java.util.concurrent.ThreadLocalRandom;

public class SimpleDrone extends Drone {

    private static final int MAX_DEVIATION_POSTION = 500;
    private static final int MAX_VELOCITY = 50;
    private static final int MAX_ACCELERATION = 10;
    private static final double RANGE_FACTOR = 5;

    /**
     *  -- FUNCTIONS
     */
    D3Vector limit_acceleration(D3Vector input){
        D3Vector output = input;
        // Prevent that the acceleration exteeds te maximum acceleration
        if(input.length() >= MAX_ACCELERATION){
            double correctionFactor = MAX_ACCELERATION / input.length();
            output = input.scale(correctionFactor);
        }
        return output;
    }



    void recalculateAcceleration(){
        D3Vector current_acceleration =  this.getAcceleration();
        D3Vector current_velocity = this.getVelocity();
        D3Vector output_acceleration = this.getAcceleration();
        D3Vector current_position = this.getPosition();

        //
        if (current_acceleration.length() == 0 && current_velocity.length() == 0){
            double x = ThreadLocalRandom.current().nextDouble(-MAX_ACCELERATION, MAX_ACCELERATION);
            double y = ThreadLocalRandom.current().nextDouble(-MAX_ACCELERATION, MAX_ACCELERATION);
            double z = ThreadLocalRandom.current().nextDouble(-MAX_ACCELERATION, MAX_ACCELERATION);
            output_acceleration = new D3Vector(x, y, z);
        }

        // Check velocity
        if (current_velocity.length() >= MAX_VELOCITY){
            output_acceleration = new D3Vector();
        }

        // Change acceleration if velocity is close to the maximum velocity
        if (current_velocity.length() >= (MAX_VELOCITY - (MAX_VELOCITY * 0.1))) {
            double factor = 0.25;
            output_acceleration = current_acceleration.scale(factor);
        }


        double aantal_seconden_tot_nul = current_velocity.length() / MAX_ACCELERATION;
        D3Vector berekende_position = current_position;
        D3Vector berekende_snelheid = current_velocity;
        D3Vector berekende_vertraging = limit_acceleration(new D3Vector(-1 * berekende_snelheid.getX(), -1 * berekende_snelheid.getY(), -1 * berekende_snelheid.getZ()));

        for (int i = 0; i < aantal_seconden_tot_nul; i++){
            berekende_position = berekende_position.add(berekende_snelheid);
            berekende_snelheid = berekende_snelheid.add(berekende_vertraging);
        }

        if (Math.abs(berekende_position.getX()-MAX_DEVIATION_POSTION) >= MAX_DEVIATION_POSTION
                || Math.abs(berekende_position.getY()-MAX_DEVIATION_POSTION) >= MAX_DEVIATION_POSTION
                || Math.abs(berekende_position.getZ()-MAX_DEVIATION_POSTION) >= MAX_DEVIATION_POSTION){
            output_acceleration = berekende_vertraging;
        }


        // Check positions | if maximum deviation is archieved then change acceleration in opposite direction
        if (Math.abs(current_position.getX()-MAX_DEVIATION_POSTION) >= MAX_DEVIATION_POSTION-20
                || Math.abs(current_position.getY()-MAX_DEVIATION_POSTION) >= MAX_DEVIATION_POSTION-20
                || Math.abs(current_position.getZ()-MAX_DEVIATION_POSTION) >= MAX_DEVIATION_POSTION-20){


            double x = ThreadLocalRandom.current().nextDouble(-MAX_ACCELERATION, MAX_ACCELERATION);//current_acceleration.getX();
            double y = ThreadLocalRandom.current().nextDouble(-MAX_ACCELERATION, MAX_ACCELERATION);//current_acceleration.getY();
            double z = ThreadLocalRandom.current().nextDouble(-MAX_ACCELERATION, MAX_ACCELERATION);//current_acceleration.getZ();

            if( current_position.getX() >= MAX_DEVIATION_POSTION-20){
                D3Vector next_postion = current_position.add(current_velocity);
                D3Vector next_position_accelerated = current_position.add(current_velocity.add(current_acceleration));
                if(next_postion.getX() <= next_position_accelerated.getX()){
                    // Er wordt nog niet afgeremd. Vertragen.
                    x = -MAX_ACCELERATION; //ThreadLocalRandom.current().nextDouble(-MAX_ACCELERATION, 0);
                }
            }

            if( (current_position.getX()-MAX_DEVIATION_POSTION) <= -MAX_DEVIATION_POSTION-20){
                D3Vector next_postion = current_position.add(current_velocity);
                D3Vector next_position_accelerated = current_position.add(current_velocity.add(current_acceleration));
                if(next_postion.getX() >= next_position_accelerated.getX()){
                    // Er wordt nog niet afgeremd. Vertragen.
                    x = MAX_ACCELERATION;//ThreadLocalRandom.current().nextDouble(0, MAX_ACCELERATION);
                }
            }
            if( current_position.getY() >= MAX_DEVIATION_POSTION-20){
                D3Vector next_postion = current_position.add(current_velocity);
                D3Vector next_position_accelerated = current_position.add(current_velocity.add(current_acceleration));
                if(next_postion.getY() <= next_position_accelerated.getY()){
                    // Er wordt nog niet afgeremd. Vertragen.
                    y = -MAX_ACCELERATION; //ThreadLocalRandom.current().nextDouble(-MAX_ACCELERATION, 0);
                }
            }

            if( (current_position.getY()-MAX_DEVIATION_POSTION) <= -MAX_DEVIATION_POSTION-20){
                D3Vector next_postion = current_position.add(current_velocity);
                D3Vector next_position_accelerated = current_position.add(current_velocity.add(current_acceleration));
                if(next_postion.getY() >= next_position_accelerated.getY()){
                    // Er wordt nog niet afgeremd. Vertragen.
                    y = MAX_ACCELERATION;//ThreadLocalRandom.current().nextDouble(0, MAX_ACCELERATION);
                }
            }



            if( current_position.getZ() >= MAX_DEVIATION_POSTION-20){
                D3Vector next_postion = current_position.add(current_velocity);
                D3Vector next_position_accelerated = current_position.add(current_velocity.add(current_acceleration));
                if(next_postion.getZ() <= next_position_accelerated.getZ()){
                    // Er wordt nog niet afgeremd. Vertragen.
                    z = -MAX_ACCELERATION; //ThreadLocalRandom.current().nextDouble(-MAX_ACCELERATION, 0);
                }
            }

            if( (current_position.getZ()-MAX_DEVIATION_POSTION) <= -MAX_DEVIATION_POSTION-20){
                D3Vector next_postion = current_position.add(current_velocity);
                D3Vector next_position_accelerated = current_position.add(current_velocity.add(current_acceleration));
                if(next_postion.getZ() >= next_position_accelerated.getZ()){
                    // Er wordt nog niet afgeremd. Vertragen.
                    z = MAX_ACCELERATION; //ThreadLocalRandom.current().nextDouble(0, MAX_ACCELERATION);
                }
            }

            output_acceleration = new D3Vector(x, y, z);



        }

        // Prevent that the acceleration exteeds te maximum acceleration
        if(output_acceleration.length() >= MAX_ACCELERATION){
            double correctionFactor = MAX_ACCELERATION / output_acceleration.length();
            output_acceleration = output_acceleration.scale(correctionFactor);
        }

        this.setAcceleration(output_acceleration);
    }
}