package org.inaetics.dronessimulator.drone.components.gps;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.common.Settings;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import java.io.IOException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

/**
 * The GPS drone component
 */
@Log4j
@NoArgsConstructor //OSGi constructor
@AllArgsConstructor //Testing constructor
public class GPS implements MessageHandler {
    /**
     * Reference to the Subscriber bundle
     */
    private volatile Subscriber subscriber;
    /**
     * Reference to the Drone Init bundle
     */
    private volatile DroneInit drone;

    private Set<GPSCallback> callbacks = new HashSet<>();

    private StateMessage previousMessage;

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
                double deltaNow = ChronoUnit.MILLIS.between(stateMessage.getTimestamp(), LocalTime.now());
                if (previousMessage != null && deltaNow > Settings.TICK_TIME) {
                    //Use the previous message to make a better guess of the location the drone probably is.
                    double deltaMessages = ChronoUnit.MILLIS.between(previousMessage.getTimestamp(), stateMessage
                            .getTimestamp());

                    D3Vector deltaAcceleration = stateMessage.getAcceleration().get().normalize().scale(stateMessage
                            .getAcceleration().get().sub(previousMessage.getAcceleration().get()).length() / deltaMessages);
                    D3Vector estimatedAcceleration = stateMessage.getAcceleration().get().add(deltaAcceleration
                            .scale(deltaNow / 1000));
                    setAcceleration(estimatedAcceleration);

                    D3Vector deltaVelocity = stateMessage.getVelocity().get().normalize().scale(stateMessage
                            .getVelocity().get().sub(previousMessage.getVelocity().get()).length() / deltaMessages);
                    D3Vector estimatedVelocity = stateMessage.getVelocity().get().add(deltaVelocity
                            .scale(deltaNow / 1000)).add(estimatedAcceleration.scale(Settings.TICK_TIME / 1000d));
                    setVelocity(estimatedVelocity);


                    D3Vector deltaPosition = stateMessage.getPosition().get().normalize().scale(stateMessage
                            .getPosition().get().sub(previousMessage.getPosition().get()).length() / deltaMessages);
                    D3Vector estimatedPosition = stateMessage.getPosition().get().add(deltaPosition
                            .scale(deltaNow / 1000)).add(estimatedVelocity.scale(Settings.TICK_TIME / 1000d));
                    setPosition(estimatedPosition);
                } else {
                    if (stateMessage.getPosition().isPresent()) {
                        this.setPosition(stateMessage.getPosition().get());
                    }
                    if (stateMessage.getAcceleration().isPresent()) {
                        this.setAcceleration(stateMessage.getAcceleration().get());
                    }
                    if (stateMessage.getVelocity().isPresent()) {
                        this.setVelocity(stateMessage.getVelocity().get());
                    }
                    if (stateMessage.getDirection().isPresent()) {
                        this.setDirection(stateMessage.getDirection().get());
                    }
                }
                previousMessage = stateMessage;
                //Run callbacks
                callbacks.forEach(callback -> callback.run(stateMessage));
            }
        }
    }

    public final void registerCallback(GPSCallback callback) {
        callbacks.add(callback);
    }

    @FunctionalInterface
    public interface GPSCallback {
        void run(StateMessage newState);
    }
}
