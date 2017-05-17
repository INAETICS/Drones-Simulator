package org.inaetics.dronessimulator.drone.components.gun;

import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.MovementMessage;
import org.inaetics.dronessimulator.drone.DroneInit;
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


    public void sendAcceleration(D3Vector acceleration){

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
