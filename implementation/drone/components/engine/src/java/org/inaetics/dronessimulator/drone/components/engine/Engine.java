package org.inaetics.dronessimulator.drone.components.engine;

import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.MovementMessage;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;

import java.io.IOException;

public class Engine {
    private final static Logger logger = Logger.getLogger(Engine.class);

    private volatile Publisher m_publisher;
    private volatile DroneInit m_drone;

    private static final int MAX_VELOCITY = 20;
    private static final int MAX_ACCELERATION = 10;

    public int getMaxVelocity(){
        return MAX_VELOCITY;
    }

    public int getMaxAcceleration(){
        return MAX_ACCELERATION;
    }

    public D3Vector limit_acceleration(D3Vector input){
        D3Vector output = input;
        // Prevent that the acceleration exteeds te maximum acceleration
        if(input.length() > this.getMaxAcceleration()){
            double correctionFactor = this.getMaxAcceleration() / input.length();
            output = input.scale(correctionFactor);
        }
        return output;
    }

    public D3Vector maximize_acceleration(D3Vector input){
        D3Vector output = input;
        // Prevent that the acceleration exteeds te maximum acceleration
        if(input.length() < this.getMaxAcceleration()){
            double correctionFactor =  this.getMaxAcceleration() / input.length();
            output = input.scale(correctionFactor);
        }
        return output;
    }

    public D3Vector stagnate_acceleration(D3Vector input){
        D3Vector output = input;
        return output;
    }


    public void changeAcceleration(D3Vector input_acceleration){
        D3Vector acceleration = input_acceleration;

        acceleration = this.limit_acceleration(acceleration);
        acceleration = this.stagnate_acceleration(acceleration);
        

        MovementMessage msg = new MovementMessage();
        msg.setAcceleration(acceleration);
        msg.setIdentifier(m_drone.getIdentifier());

        try {
            m_publisher.send(MessageTopic.MOVEMENTS, msg);
        } catch (IOException e) {
            logger.fatal(e);
        }
    }
}
