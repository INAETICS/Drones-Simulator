package org.inaetics.dronessimulator.drone.components.engine;

import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.MovementMessage;
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

    public static final int MAX_VELOCITY = 20;
    public static final int MAX_ACCELERATION = 10;

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


    public void changeAcceleration(D3Vector acceleration){
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
