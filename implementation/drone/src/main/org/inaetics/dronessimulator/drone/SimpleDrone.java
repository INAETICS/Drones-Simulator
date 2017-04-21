package org.inaetics.dronessimulator.drone;

public class RoombaDrone extends Drone {

    private static final int MAX_DEVIATION_POSTION = 1000;
    private static final int MAX_VELOCITY = 100;
    private static final int MAX_ACCELERATION = 10;
    private static final double RANGE_FACTOR = 0.2

    /**
     *  -- FUNCTIONS
     */
    private void recalculatAcceleration(){
        D3Vector current_acceleration =   self.getAcceleration();
        D3Vector output_acceleration = self.getAcceleration();
        D3Vector current_position = self.getPostion();

        // Check velocity
        if (current_velocity.length() >= MAX_VELOCITY){
            output_acceleration = new D3Vector();
        }

        // Change acceleration if velocity is close to the maximum velocity
        if (current_velocity.length() >= (MAX_VELOCITY - (MAX_VELOCITY * 0.1))) {
            double factor = 0.25
            output_acceleration = current_acceleration.scale(factor)
        }

        // Speed up acceleration if position is far from 'wall' and velocity is towards wall
            // only if movement is towards wall

        // Slow down acceleration if position is close to 'wall' and velocity is towards wall
        if(abs(current_position.getX()) == MAX_DEVIATION_POSTION ||
                abs(current_position.getY()) == MAX_DEVIATION_POSTION ||
                abs(current_position.getZ() == MAX_DEVIATION_POSTION)){
            if(/** Move towards wall*/){
                // Slow down acceleration + how to determine factor?
            }
        }
            // only if movements is towards wall



        // Check postions | if maximum deviation is archieved then change acceleration in opposite direction
        if (abs(current_position.getX()) >= MAX_DEVIATION_POSTION){
            double x = (- current_velocity.getX()) * ThreadLocalRandom.current().nextDouble(-RANGE_FACTOR, RANGE_FACTOR);
            output_acceleration = new D3Vector(x, current_acceleration.getY(),current_acceleration.getZ())
        }
        if (abs(current_position.getY()) >= MAX_DEVIATION_POSTION){
            double y = = (- current_velocity.getY()) * ThreadLocalRandom.current().nextDouble(-RANGE_FACTOR, RANGE_FACTOR);
            output_acceleration = new D3Vector(current_acceleration.getX(), y,current_acceleration.getZ())
        }

        if (abs(current_position.getZ()) >= MAX_DEVIATION_POSTION){
            double z = (- current_velocity.getX()) * ThreadLocalRandom.current().nextDouble(-RANGE_FACTOR, RANGE_FACTOR);
            acceleration_z = current_velocity.getX() * -1 * ThreadLocalRandom.current().nextDouble(-RANGE_FACTOR, RANGE_FACTOR);
        }

        // Prevent that the acceleration exteeds te maximum acceleration
        if(output_acceleration.length() >= MAX_ACCELERATION){
            double correctionFactor = MAX_ACCELERATION / input_acceleration.length();
            output_acceleration = input_acceleration.scale(correctionFactor);
        }

        this.setAcceleration(output_acceleration);
    }
}