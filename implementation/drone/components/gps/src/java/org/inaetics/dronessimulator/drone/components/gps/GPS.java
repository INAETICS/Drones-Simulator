package org.inaetics.dronessimulator.drone.components.gps;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import java.io.IOException;

/**
 * The GPS drone component
 */
@Log4j
public class GPS implements MessageHandler {
    /**
     * Reference to the Subscriber bundle
     */
    private volatile Subscriber subscriber;
    /**
     * Reference to the Drone Init bundle
     */
    private volatile DroneInit drone;

    /**
     * Last known position of this drone in the architecture
     */
    @Getter @Setter
    private volatile D3Vector position = new D3Vector();
    /**
     * Last known velocity of this drone in the architecture
     */
    @Getter @Setter
    private volatile D3Vector velocity = new D3Vector();
    /**
     * Last known acceleration of this drone in the architecture
     */
    @Getter @Setter
    private volatile D3Vector acceleration = new D3Vector();
    /**
     * Last known direction of this drone in the architecture
     */
    @Getter @Setter
    private volatile D3PolarCoordinate direction = new D3PolarCoordinate();


    /**
     * FELIX CALLBACKS
     */
    public void start() {
        try {
            this.subscriber.addTopic(MessageTopic.STATEUPDATES);
        } catch (IOException e) {
            log.fatal(e);
        }
        this.subscriber.addHandler(StateMessage.class, this);
    }

    /**
     * -- MESSAGEHANDLER
     */
    public void handleMessage(Message message) {
        if (message instanceof StateMessage){
            StateMessage stateMessage = (StateMessage) message;
            if (stateMessage.getIdentifier().equals(this.drone.getIdentifier())) {
                if (stateMessage.getPosition().isPresent()) {
                    this.setPosition(stateMessage.getPosition().get());
                }
                if (stateMessage.getAcceleration().isPresent()) {
                    this.setAcceleration(stateMessage.getAcceleration().get());
                }
                if (stateMessage.getVelocity().isPresent()) {
                    this.setVelocity(stateMessage.getVelocity().get());
                }
                if(stateMessage.getDirection().isPresent()){
                    this.setDirection(stateMessage.getDirection().get());
                }
            }
        }
    }

}
