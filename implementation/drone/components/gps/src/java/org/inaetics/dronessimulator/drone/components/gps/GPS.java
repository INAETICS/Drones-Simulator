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
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import java.io.IOException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * The GPS drone component
 */
@Log4j
@NoArgsConstructor //OSGi constructor
@AllArgsConstructor //Testing constructor
public class GPS implements MessageHandler<StateMessage> {
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
    @Getter
    @Setter
    private volatile D3Vector position = new D3Vector();
    /**
     * Last known velocity of this drone in the architecture
     */
    @Getter
    @Setter
    private volatile D3Vector velocity = new D3Vector();
    /**
     * Last known acceleration of this drone in the architecture
     */
    @Getter
    @Setter
    private volatile D3Vector acceleration = new D3Vector();
    /**
     * Last known direction of this drone in the architecture
     */
    @Getter
    @Setter
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
    public void handleMessage(StateMessage message) {
        if (message != null && message.getIdentifier().equals(this.drone.getIdentifier())) {
            //Prepare some variables
            double deltaNow = ChronoUnit.MILLIS.between(message.getTimestamp(), LocalTime.now());
            Optional<D3Vector> optionalPosition = message.getPosition();
            Optional<D3Vector> optionalVelocity = message.getVelocity();
            Optional<D3Vector> optionalAccelration = message.getAcceleration();
            Optional<D3PolarCoordinate> optionalDirection = message.getDirection();

            //Check if the message is recent
            if (previousMessage != null && deltaNow > Settings.TICK_TIME) {
                double deltaMessages = ChronoUnit.MILLIS.between(previousMessage.getTimestamp(), message.getTimestamp());
                Optional<D3Vector> optionalPreviousPosition = previousMessage.getPosition();
                Optional<D3Vector> optionalPreviousVelocity = previousMessage.getVelocity();
                Optional<D3Vector> optionalPreviousAcceleration = previousMessage.getAcceleration();

                if (deltaMessages <= 0) {
                    //We cannot use two messages that were send at the same time since this will create a NaN. To
                    // avoid getting this multiple times, we do not update the last message.
                    return;
                }

                //Use the previous message to make a better guess of the location the drone probably is.
                if (optionalAccelration.isPresent() && optionalPreviousAcceleration.isPresent()) {
                    D3Vector deltaAcceleration;
                    if (optionalAccelration.get().equals(D3Vector.ZERO)) {
                        deltaAcceleration = D3Vector.ZERO;
                    } else {
                        deltaAcceleration = optionalAccelration.get().normalize().scale(optionalAccelration.get().sub(optionalPreviousAcceleration.get()).length() / deltaMessages);
                    }
                    D3Vector estimatedAcceleration = optionalAccelration.get().add(deltaAcceleration.scale(deltaNow / 1000));
                    setAcceleration(estimatedAcceleration);

                    if (optionalVelocity.isPresent() && optionalPreviousVelocity.isPresent()) {
                        D3Vector deltaVelocity;
                        if (optionalAccelration.get().equals(D3Vector.ZERO)) {
                            deltaVelocity = D3Vector.ZERO;
                        } else {
                            deltaVelocity = optionalVelocity.get().normalize().scale(optionalVelocity.get().sub(optionalPreviousVelocity.get()).length() / deltaMessages);
                        }
                        D3Vector estimatedVelocity = optionalVelocity.get().add(deltaVelocity.scale(deltaNow / 1000)).add(estimatedAcceleration.scale(Settings.getTickTime(ChronoUnit.SECONDS)));
                        setVelocity(estimatedVelocity);

                        if (optionalPosition.isPresent() && optionalPreviousPosition.isPresent()) {
                            D3Vector deltaPosition;
                            if (optionalAccelration.get().equals(D3Vector.ZERO)) {
                                deltaPosition = D3Vector.ZERO;
                            } else {
                                deltaPosition = optionalPosition.get().normalize().scale(optionalPosition.get().sub(optionalPreviousPosition.get()).length() / deltaMessages);
                            }
                            D3Vector estimatedPosition = optionalPosition.get().add(deltaPosition.scale(deltaNow / 1000)).add(estimatedVelocity.scale(Settings.getTickTime(ChronoUnit.SECONDS)));
                            setPosition(estimatedPosition);
                        }
                    }
                }
            } else {
                optionalPosition.ifPresent(this::setPosition);
                optionalAccelration.ifPresent(this::setAcceleration);
                optionalVelocity.ifPresent(this::setVelocity);
                optionalDirection.ifPresent(this::setDirection);
            }
            previousMessage = message;
            //Run callbacks
            callbacks.forEach(callback -> callback.run(message));
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
