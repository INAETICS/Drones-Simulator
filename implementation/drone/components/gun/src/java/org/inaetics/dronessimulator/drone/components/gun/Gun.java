package org.inaetics.dronessimulator.drone.components.gun;

import org.inaetics.dronessimulator.common.D3PoolCoordinate;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.CreateEntity;
import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.MovementMessage;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import java.io.IOException;

/**
 * Created by mart on 17-5-17.
 */
public class Gun {
    private volatile Subscriber m_subscriber;
    private volatile Publisher m_publisher;
    private volatile DroneInit m_drone;
    private final double GUN_SPEED = 50.0;
    private final double MAX_DISTANCE = 100;


    public double getMaxDistance(){
        return MAX_DISTANCE;
    }


    D3Vector optimize_acceleration(D3Vector input, double max_length){
        double correctionFactor = 0;
        // Prevent that the acceleration exteeds te maximum acceleration
        if(input.length() > max_length){
            correctionFactor = max_length / input.length();
        } else if(input.length() < max_length){
            correctionFactor = input.length() / max_length;
        }
        return input.scale(correctionFactor);
    }

    public void fireBullet(D3PoolCoordinate direction){
        CreateEntity msg = new CreateEntity();
        msg.setIdentifier();
        msg.setType(EntityType.BULLET);
        msg.setDirection(direction);
        msg.setVelocity(this.optimize_acceleration(direction.toVector(), GUN_SPEED));
        msg.setPosition(m_gps.getPosition());
        msg.setAcceleration(new D3Vector());


        try{
            m_publisher.send(MessageTopic.MOVEMENTS, msg);
        } catch(IOException e){
            System.out.println("Exception");
        }
    }

}
