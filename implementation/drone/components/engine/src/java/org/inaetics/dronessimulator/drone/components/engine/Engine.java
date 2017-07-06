package org.inaetics.dronessimulator.drone.components.engine;

import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.MovementMessage;
import org.inaetics.dronessimulator.drone.components.gps.GPS;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import java.io.IOException;

/**
 * Created by mart on 17-5-17.
 */
public class Engine {
    private volatile Publisher m_publisher;
    private volatile DroneInit m_drone;
    private volatile GPS m_gps;

    private static final int MAX_VELOCITY = 20;
    private static final int MAX_ACCELERATION = 10;

    public int getMaxVelocity(){
        return MAX_VELOCITY;
    }

    public int getMaxAcceleration(){
        return MAX_ACCELERATION;
    }

    /**
     * Prevents that the acceleration exteeds the maximum value
     * @param input
     * @return
     */
    public D3Vector limit_acceleration(D3Vector input){
        D3Vector output = input;
        // Prevent that the acceleration exteeds te maximum acceleration
        if(input.length() > this.getMaxAcceleration()){
            double correctionFactor = this.getMaxAcceleration() / input.length();
            output = input.scale(correctionFactor);
        }
        return output;
    }

    /**
     * Maximizes the acceleration to an acceleration in the same direction but with the maximum possible acceleration.
     * @param input acceleration as a D3Vector
     * @return optimized acceleration as a D3Vector
     */
    public D3Vector maximize_acceleration(D3Vector input){
        D3Vector output = input;
        // Prevent that the acceleration exteeds te maximum acceleration
        if(input.length() < this.getMaxAcceleration()){
            double correctionFactor =  this.getMaxAcceleration() / input.length();
            output = input.scale(correctionFactor);
        }
        return output;
    }

    /**
     * Limits the velocity when the maximum velocity is archieved.
     * @param input acceleration as a D3Vector
     * @return
     */
    private D3Vector limit_velocity(D3Vector input){
        D3Vector output = input;
        // Check velocity
        if (m_gps.getVelocity().length() >= this.getMaxVelocity() && m_gps.getVelocity().add(input).length() >= m_gps.getVelocity().length() ){
            output = new D3Vector();
        }
        return output;
    }


    /**
     * Stagnate the acceleration when the velocity is at 90% of the maximum velocity.
     * @param input acceleration as a D3Vector
     * @return
     */
    public D3Vector stagnate_acceleration(D3Vector input){
        D3Vector output = input;
        // Change acceleration if velocity is close to the maximum velocity
        if (m_gps.getVelocity().length() >= (this.getMaxVelocity() - (this.getMaxVelocity() * 0.1))) {
            double factor = 0.25;
            D3Vector test_acceleration = m_gps.getAcceleration().scale(factor);
            if(m_gps.getVelocity().add(test_acceleration).length() <= m_gps.getVelocity().add(input).length()){
                output = test_acceleration;
            }
        }
        return output;
    }


    public void changeAcceleration(D3Vector input_acceleration){
        D3Vector acceleration = input_acceleration;

        acceleration = this.limit_acceleration(acceleration);
        acceleration = this.limit_velocity(acceleration);
        acceleration = this.stagnate_acceleration(acceleration);


        MovementMessage msg = new MovementMessage();
        msg.setAcceleration(acceleration);
        msg.setIdentifier(m_drone.getIdentifier());

        try {
            m_publisher.send(MessageTopic.MOVEMENTS, msg);
        } catch (IOException e) {
            System.out.println("Exception");
        }
    }
}
