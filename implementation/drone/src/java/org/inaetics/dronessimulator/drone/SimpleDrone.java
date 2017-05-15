package org.inaetics.dronessimulator.drone;

import org.inaetics.dronessimulator.common.D3Vector;

import java.util.concurrent.ThreadLocalRandom;

public class SimpleDrone extends Drone {

    private static final int MAX_DEVIATION_POSTION = 200;
    private static final int MAX_VELOCITY = 100;
    private static final int MAX_ACCELERATION = 20;
    private static final double RANGE_FACTOR = 5;

    /**
     *  -- FUNCTIONS
     */
    D3Vector limit_acceleration(D3Vector input){
        D3Vector output = input;
        // Prevent that the acceleration exteeds te maximum acceleration
        if(input.length() > MAX_ACCELERATION){
            double correctionFactor = MAX_ACCELERATION / input.length();
            output = input.scale(correctionFactor);
        }
        return output;
    }

    D3Vector maximize_acceleration(D3Vector input){
        D3Vector output = input;
        // Prevent that the acceleration exteeds te maximum acceleration
        if(input.length() < MAX_ACCELERATION){
            double correctionFactor =  input.length() / MAX_ACCELERATION;
            output = input.scale(correctionFactor);
        }
        return output;
    }



    D3Vector recalculateAcceleration(){
        D3Vector current_acceleration =  this.getAcceleration();
        D3Vector current_velocity = this.getVelocity();
        D3Vector output_acceleration = this.getAcceleration();
        D3Vector current_position = this.getPosition();

        output_acceleration = this.maximize_acceleration(current_acceleration);

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

        if(current_velocity.add(output_acceleration).length() <= current_velocity.length()){
            // drone is aan het afremmen

        }

        //output_acceleration = limit_acceleration(output_acceleration.scale(MAX_ACCELERATION));


        double aantal_seconden_tot_nul = current_velocity.length() / MAX_ACCELERATION;
        D3Vector berekende_vertraging = maximize_acceleration(limit_acceleration(current_velocity.scale(-1)));
        D3Vector berekende_position = current_velocity.scale((1/2) * berekende_vertraging.length() * Math.pow(aantal_seconden_tot_nul, 2)).add(current_position);

        if (Math.abs(berekende_position.getX()-MAX_DEVIATION_POSTION) >= MAX_DEVIATION_POSTION
                || Math.abs(berekende_position.getY()-MAX_DEVIATION_POSTION) >= MAX_DEVIATION_POSTION
                || Math.abs(berekende_position.getZ()-MAX_DEVIATION_POSTION) >= MAX_DEVIATION_POSTION){
            output_acceleration = berekende_vertraging;
        }


        // Check positions | if maximum deviation is archieved then change acceleration in opposite direction
        if (Math.abs(current_position.getX()-MAX_DEVIATION_POSTION) >= MAX_DEVIATION_POSTION
                || Math.abs(current_position.getY()-MAX_DEVIATION_POSTION) >= MAX_DEVIATION_POSTION
                || Math.abs(current_position.getZ()-MAX_DEVIATION_POSTION) >= MAX_DEVIATION_POSTION){


            double x = ThreadLocalRandom.current().nextDouble(-MAX_ACCELERATION, MAX_ACCELERATION);//current_acceleration.getX();
            double y = ThreadLocalRandom.current().nextDouble(-MAX_ACCELERATION, MAX_ACCELERATION);//current_acceleration.getY();
            double z = ThreadLocalRandom.current().nextDouble(-MAX_ACCELERATION, MAX_ACCELERATION);//current_acceleration.getZ();

            if( current_position.getX() >= MAX_DEVIATION_POSTION){
                D3Vector next_postion = current_position.add(current_velocity);
                D3Vector next_position_accelerated = current_position.add(current_velocity.add(current_acceleration));
                if(next_postion.getX() <= next_position_accelerated.getX()){
                    // Er wordt nog niet afgeremd. Vertragen.
                    x = -MAX_ACCELERATION; //ThreadLocalRandom.current().nextDouble(-MAX_ACCELERATION, 0);
                } else{
                    x = current_acceleration.getX();
                }
                x = - MAX_ACCELERATION;
            }

            if( (current_position.getX()-MAX_DEVIATION_POSTION) <= -MAX_DEVIATION_POSTION){
                D3Vector next_postion = current_position.add(current_velocity);
                D3Vector next_position_accelerated = current_position.add(current_velocity.add(current_acceleration));
                if(next_postion.getX() >= next_position_accelerated.getX()){
                    // Er wordt nog niet afgeremd. Vertragen.
                    x = MAX_ACCELERATION;//ThreadLocalRandom.current().nextDouble(0, MAX_ACCELERATION);
                } else{
                    x = current_acceleration.getX();
                }
                x = MAX_ACCELERATION;
            }
            if( current_position.getY() >= MAX_DEVIATION_POSTION){
                D3Vector next_postion = current_position.add(current_velocity);
                D3Vector next_position_accelerated = current_position.add(current_velocity.add(current_acceleration));
                if(next_postion.getY() <= next_position_accelerated.getY()){
                    // Er wordt nog niet afgeremd. Vertragen.
                    y = -MAX_ACCELERATION; //ThreadLocalRandom.current().nextDouble(-MAX_ACCELERATION, 0);
                } else{
                    y = current_acceleration.getY();
                }
                y = -MAX_ACCELERATION;
            }

            if( (current_position.getY()-MAX_DEVIATION_POSTION) <= -MAX_DEVIATION_POSTION){
                D3Vector next_postion = current_position.add(current_velocity);
                D3Vector next_position_accelerated = current_position.add(current_velocity.add(current_acceleration));
                if(next_postion.getY() >= next_position_accelerated.getY()){
                    // Er wordt nog niet afgeremd. Vertragen.
                    y = MAX_ACCELERATION;//ThreadLocalRandom.current().nextDouble(0, MAX_ACCELERATION);
                } else{
                    y = current_acceleration.getY();
                }
                y = MAX_ACCELERATION;
            }

            if( current_position.getZ() >= MAX_DEVIATION_POSTION){
                D3Vector next_postion = current_position.add(current_velocity);
                D3Vector next_position_accelerated = current_position.add(current_velocity.add(current_acceleration));
                if(next_postion.getZ() <= next_position_accelerated.getZ()){
                    // Er wordt nog niet afgeremd. Vertragen.
                    z = -MAX_ACCELERATION; //ThreadLocalRandom.current().nextDouble(-MAX_ACCELERATION, 0);
                } else{
                    z = current_acceleration.getZ();
                }
                z = -MAX_ACCELERATION;
            }

            if( (current_position.getZ()-MAX_DEVIATION_POSTION) <= -MAX_DEVIATION_POSTION){
                D3Vector next_postion = current_position.add(current_velocity);
                D3Vector next_position_accelerated = current_position.add(current_velocity.add(current_acceleration));
                if(next_postion.getZ() >= next_position_accelerated.getZ()){
                    // Er wordt nog niet afgeremd. Vertragen.
                    z = MAX_ACCELERATION; //ThreadLocalRandom.current().nextDouble(0, MAX_ACCELERATION);
                } else{
                    z = current_acceleration.getZ();
                }
                z = MAX_ACCELERATION;
            }

            output_acceleration = new D3Vector(x, y, z);



        }

        output_acceleration = this.limit_acceleration(output_acceleration);
        return output_acceleration;
    }
}