package org.inaetics.dronessimulator.drone;
import org.inaetics.dronessimulator.common.*;

import java.util.concurrent.ThreadLocalRandom;

public class SimpleDrone extends Drone {

    private static final int MAX_DEVIATION_POSTION = 1000;
    private static final int MAX_VELOCITY = 100;
    private static final int MAX_ACCELERATION = 10;
    private static final double RANGE_FACTOR = 0.2;

    /**
     *  -- FUNCTIONS
     */
    void recalculateAcceleration(){
        D3Vector current_acceleration =  this.getAcceleration();
        D3Vector current_velocity = this.getVelocity();
        D3Vector output_acceleration = this.getAcceleration();
        D3Vector current_position = this.getPosition();

        //
        if (current_acceleration.length() == 0 && current_velocity.length() == 0 && current_position.length() == 0){
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

        // Check positions | if maximum deviation is archieved then change acceleration in opposite direction
        if (Math.abs(current_position.getX()) >= MAX_DEVIATION_POSTION){
            double x = (- current_velocity.getX()) * ThreadLocalRandom.current().nextDouble(-RANGE_FACTOR, RANGE_FACTOR);
            output_acceleration = new D3Vector(x, current_acceleration.getY(),current_acceleration.getZ());
        }
        if (Math.abs(current_position.getY()) >= MAX_DEVIATION_POSTION){
            double y = (- current_velocity.getY()) * ThreadLocalRandom.current().nextDouble(-RANGE_FACTOR, RANGE_FACTOR);
            output_acceleration = new D3Vector(current_acceleration.getX(), y,current_acceleration.getZ());
        }

        if (Math.abs(current_position.getZ()) >= MAX_DEVIATION_POSTION){
            double z = (- current_velocity.getX()) * ThreadLocalRandom.current().nextDouble(-RANGE_FACTOR, RANGE_FACTOR);
            output_acceleration = new D3Vector(current_acceleration.getX(), current_acceleration.getY() , z);
        }

        // Prevent that the acceleration exteeds te maximum acceleration
        if(output_acceleration.length() >= MAX_ACCELERATION){
            double correctionFactor = MAX_ACCELERATION / output_acceleration.length();
            output_acceleration = output_acceleration.scale(correctionFactor);
        }

        this.setAcceleration(output_acceleration);
    }
}